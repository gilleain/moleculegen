package app;

import handler.DataFormat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * For a single structure in an input file, augment up to the number of atoms remaining 
 * in the difference between the input and the heavyAtomCount.
 * 
 * @author maclean
 *
 */
public class SingleInputAugmentor {

    private static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     * @param argsH the argument handler
     * @param inputFile the input file path
     * @param generator the augmenting generator
     * @param heavyAtomCount the number of heavy (non-hydrogen) atoms in the formula
     * @throws CDKException 
     * @throws IOException if the chemobject reader has an error
     */
    public static void run(
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
        // test.AtomContainerPrinter.print(parent);
        if (parent == null) {
            error("Molecule null");
        } else {
            generator.extend(parent, heavyAtomCount);
        }
    }

    private static void error(String text) {
        System.out.println(text);
    }

}
