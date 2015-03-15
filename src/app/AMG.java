package app;

import generate.AugmentingGenerator;
import handler.DataFormat;
import io.IteratingACPReader;
import io.IteratingSignatureReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

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
        AugmentingGenerator generator = AugmentingGeneratorFactory.build(argsH);
        int heavyAtomCount = generator.getHeavyAtomCount();
        
        if (argsH.isAugmentingFile() || argsH.isComparingToFile()) {
            String inputFile = argsH.getInputFilepath();
            if (inputFile == null) {
                error("No input file specified");
                return;
            } else {
            	if (argsH.isAugmentingFile()) {
	                DataFormat inputFormat = argsH.getInputFormat();
	                if (inputFormat == DataFormat.MOL) {
	                    augmentSingleInputStructure(argsH, inputFile, generator, heavyAtomCount);
	                } else {
	                    augmentMultipleInputStructures(argsH, inputFile, generator, heavyAtomCount);
	                }
            	} else {
            		startFromScratch(generator, heavyAtomCount);
            	}
            }
        } else if (argsH.isStartingFromScratch()) {
            startFromScratch(generator, heavyAtomCount);
        }
        generator.finish();
    }
    
    private static void startFromScratch(AugmentingGenerator generator, int heavyAtomCount) {
    	List<String> symbols = generator.getElementSymbols();
        String firstSymbol = symbols.get(0);
        IAtomContainer startingAtom = makeAtomInAtomContainer(firstSymbol, builder);
        generator.extend(startingAtom, heavyAtomCount);
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
            AugmentingGenerator generator,
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
            in.close();
            return;
        }
//        test.AtomContainerPrinter.print(parent);
        if (parent == null) {
            error("Molecule null");
        } else {
            generator.extend(parent, heavyAtomCount);
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
            AugmentingGenerator generator, 
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
                // XXX what about explicit Hs for the atom count?
                generator.extend(parent, heavyAtomCount);
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
            case ACP: reader = new IteratingACPReader(in, builder); break;
            default: reader = null; error("Unrecognised format"); break;
        }
        return reader;
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
