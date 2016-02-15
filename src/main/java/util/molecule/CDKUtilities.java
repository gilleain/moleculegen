package util.molecule;

import augment.atom.*;
import io.AtomContainerPrinter;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.signature.*;

/**
 * util.molecule.CDKUtilities
 * User: Steve
 * Date: 2/5/2016
 */
public class CDKUtilities {

    public static final boolean ADD_DEBUG_PRINTING = false;

    public static String atomAugmentationToString(AtomAugmentation c) {
         return atomContainerToString(c.getAugmentedObject());
    }

    public static String atomContainerToString(IAtomContainer c) {
        String canonicalSignature = new MoleculeSignature(c).toCanonicalString();
        String right = AtomContainerPrinter.toString(c);
        return canonicalSignature + " = " + right;
    }


    public static String atomToString(IAtomType  atom) {
        return atom.getAtomTypeName();
    }

}
