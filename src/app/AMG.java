package app;

import generate.AtomAugmentingGenerator;
import generate.ListerMethod;
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
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

public class AMG {
    
    private static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
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
        
        GenerateHandler handler;
        DataFormat format = argsH.getOutputFormat();
        
        if (format == DataFormat.NONE) {
            handler = new CountingHandler();
        } else {
            PrintStream outStream;
            boolean shouldNumberLines = argsH.isShouldNumberLines();
            if (argsH.isStdOut()) {
                if (format == DataFormat.SDF) {
                    handler = new SDFHandler();
                } else {
                    outStream = System.out;
                    handler = new PrintStreamStringHandler(outStream, format, shouldNumberLines);
                }
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
        
        // create the generator, with the appropriate handler and lister method
        ListerMethod listerMethod = (argsH.getListerMethod() == null)? ListerMethod.FILTER : argsH.getListerMethod();
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator(handler, listerMethod);
        
        int heavyAtomCount = setParamsFromFormula(formula, generator);
        if (heavyAtomCount < 2) {
            error("Please specify more than 1 heavy atom");
            return;
        }
        
        if (argsH.isAugmentingFile()) {
            String inputFile = argsH.getInputFilepath();
            if (inputFile == null) {
                error("No input file specified");
                return;
            } else {
                DataFormat inputFormat = argsH.getInputFormat();
                if (inputFormat == DataFormat.MOL) {
                    augmentSingleInputStructure(argsH, inputFile, generator, heavyAtomCount);
                } else {
                    augmentMultipleInputStructures(argsH, inputFile, generator, heavyAtomCount);
                }
            }
        } else if (argsH.isStartingFromScratch()) {
            List<String> symbols = generator.getElementSymbols();
            
            String firstSymbol  = symbols.get(0);
            IAtomContainer startingAtom = makeAtomInAtomContainer(firstSymbol, builder);
            generator.extend(startingAtom, 1, heavyAtomCount);
        }
        handler.finish();
    }
    
    /**
     * For a single structure in an input file, augment up to the number of atoms remaining 
     * in the difference between the input and the heavyAtomCount.
     * 
     * @param argsH the argument handler
     * @param inputFile the input file path
     * @param generator the augmenting generator
     * @param heavyAtomCount the number of heavy (non-hydrogen) atoms in the formula
     * @throws CDKException 
     * @throws IOException if the chemobject reader has an error
     */
    private static void augmentSingleInputStructure(
            ArgumentHandler argsH,
            String inputFile, 
            AtomAugmentingGenerator generator,
            int heavyAtomCount) throws CDKException, IOException {
        DataFormat inputFormat = argsH.getInputFormat();
        String filepath = argsH.getInputFilepath();
        InputStream in = new FileInputStream(filepath);
        IAtomContainer parent;
        if (inputFormat == DataFormat.MOL) {
            MDLV2000Reader reader = new MDLV2000Reader(in);
            parent = reader.read(builder.newInstance(IAtomContainer.class));
            reader.close();
        } else {
            // XXX - other single file formats?
            return;
        }
//        test.AtomContainerPrinter.print(parent);
        if (parent == null) {
            error("Molecule null");
        } else {
            generator.extend(parent, parent.getAtomCount(), heavyAtomCount);
        }
    }

    /**
     * For each structure in an input file, augment up to the number of atoms remaining 
     * in the difference between the input and the heavyAtomCount.
     * 
     * @param argsH the argument handler
     * @param inputFile the input file path
     * @param generator the augmenting generator
     * @param heavyAtomCount the number of heavy (non-hydrogen) atoms in the formula
     * @throws IOException if the iterating chemobject reader has an error
     */
    private static void augmentMultipleInputStructures(
            ArgumentHandler argsH, 
            String inputFile, 
            AtomAugmentingGenerator generator, 
            int heavyAtomCount) throws IOException {
        
        // allow for selecting a range of input from the input file
        String rangeString = argsH.getRangeString();
        int minIndex = -1;
        int maxIndex = -1;
        if (rangeString != null) {
            int colonIndex = rangeString.indexOf(":");
            if (colonIndex != -1) {
                minIndex = Integer.parseInt(rangeString.substring(0, colonIndex));
                maxIndex = Integer.parseInt(rangeString.substring(colonIndex + 1));
//                System.out.println("min " + minIndex + " max " + maxIndex);
            }
        }
        
        // get an iterating reader, and augment each structure in the file
        IIteratingChemObjectReader<IAtomContainer> reader = getInputReader(argsH, builder);
        if (reader != null) {
            int inputCount = 0;
            while (reader.hasNext()) {
                if (minIndex != -1 && inputCount < minIndex) continue;
                IAtomContainer parent = reader.next();
//                test.AtomContainerPrinter.print(parent);
                int currentAtomIndex = parent.getAtomCount();   // XXX what about explicit Hs?
                generator.extend(parent, currentAtomIndex, heavyAtomCount);
                inputCount++;
                if (maxIndex != -1 && inputCount == maxIndex) break;
            }
            reader.close();
            
        } else {
            error("Problem with the input");    // XXX
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
        Collections.sort(elementSymbols);
        generator.setElementSymbols(elementSymbols);
        
        return elementSymbols.size();
    }
    
    private static void error(String text) {
        System.out.println(text);
    }
    
    private static IAtomContainer makeAtomInAtomContainer(String elementSymbol, IChemObjectBuilder builder) {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class,(elementSymbol)));
        return ac;
    }
}
