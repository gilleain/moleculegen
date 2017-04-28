package util.molecule;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.signature.MoleculeSignature;

import augment.atom.AtomAugmentation;
import io.AtomContainerPrinter;

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
