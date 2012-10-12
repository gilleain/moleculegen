package app;

import generate.AtomAugmentingGenerator;
import handler.CountingHandler;
import handler.DataFormat;
import handler.GenerateHandler;
import handler.PrintStreamStringHandler;
import handler.SDFHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

public class AMG {
    
    public static void main(String[] args) throws CDKException, IOException {
        ArgumentHandler argsH = new ArgumentHandler();
        try {
            argsH.processArguments(args);
            run(argsH);
        } catch (ParseException pe) {
            // TODO
        }
    }
    
    public static void run(ArgumentHandler argsH) throws CDKException, IOException {
        if (argsH.isHelp()) {
            argsH.printHelp();
            return;
        }
        
        String formula = argsH.getFormula();
        if (formula == null) {
            error("Please supply a formula");
            return;
        }
        
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        AtomAugmentingGenerator generator;
        GenerateHandler handler;
        DataFormat format = argsH.getOutputFormat();
        
        if (format == DataFormat.NONE) {
            handler = new CountingHandler();
        } else {
            PrintStream outStream;
            boolean shouldNumberLines = argsH.isShouldNumberLines();
            if (argsH.isStdOut()) {
                outStream = System.out;
                handler = new PrintStreamStringHandler(outStream, format, shouldNumberLines);
            } else {
                String outputFilename = argsH.getOutputFilepath();
                if (format == DataFormat.SDF) {
                    handler = new SDFHandler(outputFilename);
                } else {
                    outStream = new PrintStream(new FileOutputStream(outputFilename));
                    handler = new PrintStreamStringHandler(outStream, format, shouldNumberLines);
                }
            }
        }
        generator = new AtomAugmentingGenerator(handler);
        
        int heavyAtomCount = setParamsFromFormula(formula, generator);
        if (heavyAtomCount < 3) {
            error("Please specify more than 3 heavy atoms");
            return;
        }
        
        if (argsH.isAugmentingFile()) {
            String inputFile = argsH.getInputFilepath();
            String rangeString = argsH.getRangeString();
            int minIndex = -1;
            int maxIndex = -1;
            if (rangeString != null) {
                int colonIndex = rangeString.indexOf(":");
                if (colonIndex != -1) {
                    minIndex = Integer.parseInt(rangeString.substring(0, colonIndex));
                    maxIndex = Integer.parseInt(rangeString.substring(colonIndex + 1));
                    System.out.println("min " + minIndex + " max " + maxIndex);
                }
            }
            if (inputFile == null) {
                error("No input file specified");
                return;
            } else {
                // TODO : single-molecule file?
                IIteratingChemObjectReader<IAtomContainer> reader = getInputReader(argsH, builder);
                if (reader != null) {
                    int inputCount = 0;
                    while (reader.hasNext()) {
                        if (minIndex != -1 && inputCount < minIndex) continue;
                        IAtomContainer parent = reader.next();
//                        test.AtomContainerPrinter.print(parent);
                        int currentAtomIndex = parent.getAtomCount();   // XXX what about Hs?
                        generator.extend(parent, currentAtomIndex, heavyAtomCount);
                        inputCount++;
                        if (maxIndex != -1 && inputCount == maxIndex) break;
                    }
                    reader.close();
                    handler.finish();
                } else {
                    error("Problem with the input");    // XXX
                }
                
            }
        } else if (argsH.isStartingFromScratch()) {
            List<String> symbols = generator.getElementSymbols();
            
            // XXX until generation from a single atom is fixed, have to do this...
            String firstE  = symbols.get(0);
            String secondE = symbols.get(1);
            
            IAtomContainer singleBond = makeEdge(firstE, secondE, IBond.Order.SINGLE, builder);
            generator.extend(singleBond, 2, heavyAtomCount);
            
            IAtomContainer doubleBond = makeEdge(firstE, secondE, IBond.Order.DOUBLE, builder);
            generator.extend(doubleBond, 2, heavyAtomCount);
            
            // XXX - if there are less than 2 carbons, this might be inefficient?
            IAtomContainer tripleBond = makeEdge(firstE, secondE, IBond.Order.TRIPLE, builder);
            generator.extend(tripleBond, 2, heavyAtomCount);
            handler.finish();
        }
    }
    
    /**
     * Get a suitable reader based on the program arguments or null if there is
     * a problem.
     * 
     * @param argsH
     * @params builder a chem object builder
     * @return
     * @throws FileNotFoundException 
     */
    private static IIteratingChemObjectReader<IAtomContainer> getInputReader(
            ArgumentHandler argsH, IChemObjectBuilder builder) throws FileNotFoundException {
        DataFormat inputFormat = argsH.getInputFormat();
        IIteratingChemObjectReader<IAtomContainer> reader;
        String filepath = argsH.getInputFilepath();
        InputStream in = new FileInputStream(filepath);
        switch (inputFormat) {
            case SMILES: reader = new IteratingSMILESReader(in, builder); break;
            case SIGNATURE: reader = new IteratingSignatureReader(in, builder); break;
            case SDF: reader = new IteratingSDFReader(in, builder); break; 
            default: reader = null; error("Unrecognised format"); break;
        }
        return reader;
    }

    /**
     * Set the hydrogen count and the heavy atom symbol string from the formula, 
     * returning the count of heavy atoms.
     * 
     * @param formulaString
     * @param generator
     * @return
     */
    private static int setParamsFromFormula(
            String formulaString, AtomAugmentingGenerator generator) {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IMolecularFormula formula = 
            MolecularFormulaManipulator.getMolecularFormula(formulaString, builder);
        List<IElement> elements = MolecularFormulaManipulator.elements(formula);
        
        // count the number of non-heavy atoms
        int hCount = 0;
        List<String> elementSymbols = new ArrayList<String>();
        for (IElement element : elements) {
            String symbol = element.getSymbol();
            int count = MolecularFormulaManipulator.getElementCount(formula, element);
            if (symbol.equals("H")) {
                hCount = count;
            } else {
                for (int i = 0; i < count; i++) {
                    elementSymbols.add(symbol);
                }
            }
        }
        generator.setHCount(hCount);
        
        // could just pass in the list of strings...
        Collections.sort(elementSymbols);
        StringBuffer buffer = new StringBuffer();
        for (String e : elementSymbols) {
            buffer.append(e);
        }
        generator.setElementString(buffer.toString());
        return elementSymbols.size();
    }
    
    private static void error(String text) {
        System.out.println(text);
    }
    
    private static IAtomContainer makeEdge(
            String elementA, String elementB, IBond.Order order, IChemObjectBuilder builder) {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class,(elementA)));
        ac.addAtom(builder.newInstance(IAtom.class,(elementA)));
        ac.addBond(0, 1, order);
        return ac;
    }

}
