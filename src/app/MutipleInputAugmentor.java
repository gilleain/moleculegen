package app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import augment.AugmentingGenerator;
import handler.DataFormat;
import io.IteratingACPReader;
import io.IteratingSignatureReader;

/**
 * For each structure in an input file, augment up to the number of atoms remaining 
 * in the difference between the input and the heavyAtomCount.
 * 
 * @author maclean
 *
 */
public class MutipleInputAugmentor {

    private static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     * @param argsH the argument handler
     * @param inputFile the input file path
     * @param generator the augmenting generator
     * @param heavyAtomCount the number of heavy (non-hydrogen) atoms in the formula
     * @throws IOException if the iterating chemobject reader has an error
     */
    public static void run(
            ArgumentHandler argsH, String inputFile, AugmentingGenerator generator) throws IOException {

        // allow for selecting a range of input from the input file
        String rangeString = argsH.getRangeString();
        int minIndex = -1;
        int maxIndex = -1;
        if (rangeString != null) {
            int colonIndex = rangeString.indexOf(":");
            if (colonIndex != -1) {
                minIndex = Integer.parseInt(rangeString.substring(0, colonIndex));
                maxIndex = Integer.parseInt(rangeString.substring(colonIndex + 1));
                // System.out.println("min " + minIndex + " max " + maxIndex);
            }
        }

        // get an iterating reader, and augment each structure in the file
        IIteratingChemObjectReader<IAtomContainer> reader = getInputReader(argsH, builder);
        if (reader != null) {
            int inputCount = 0;
            while (reader.hasNext()) {
                if (minIndex != -1 && inputCount < minIndex) continue;
                IAtomContainer parent = reader.next();
                // test.AtomContainerPrinter.print(parent);
                // XXX what about explicit Hs for the atom count?
                generator.run(parent);
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

}
