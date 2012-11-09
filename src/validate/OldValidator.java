package validate;

import io.AtomContainerPrinter;

import java.util.Arrays;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;



/**
 * BROKEN : This validator used a (possibly) incorrect canonicalization routine.
 * Instead of checking the canonical deletion object (H, w) for isomorphism to the 
 * inverse of the augmentation (G, v) it checked isomorphism of the parent to the
 * canonically deleted child. 
 * 
 * @author maclean
 * @deprecated
 *
 */
public class OldValidator  {
    
    private int hCount;
    
    public OldValidator() {
        hCount = 0;
    }

    public boolean isValidMol(IAtomContainer atomContainer, int size) {
        // TODO!
//        System.out.println("validating " + test.AtomContainerPrinter.toString(atomContainer));
        return atomContainer.getAtomCount() == size && hydrogensCorrect(atomContainer);
    }

    private boolean hydrogensCorrect(IAtomContainer atomContainer) {
//        if (hCount < 1) return true;    // XXX
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
            CDKHydrogenAdder adder = 
                CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
            adder.addImplicitHydrogens(atomContainer);
            int actualCount = 0;
            for (IAtom atom : atomContainer.atoms()) {
                actualCount += AtomContainerManipulator.countHydrogens(atomContainer, atom);
                if (actualCount > hCount) {
                    return false;
                }
            }
            return actualCount == hCount;
        } catch (CDKException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isCanonical(IAtomContainer parent, IAtomContainer child, String parentSignature) {
        MoleculeSignature molSig = new MoleculeSignature(child);
        int[] labels = molSig.getCanonicalLabels();
        int size = labels.length - 1;
//        int vertexToDelete = labels[size];
        int vertexToDelete = find(labels, size);
        
//        System.out.println(java.util.Arrays.toString(labels));

        IAtomContainer canonicalParent = null;
        
        try { canonicalParent = (IAtomContainer) child.clone(); } catch (Exception e) {}
//        canonicalParent = reconstruct(molSig.toCanonicalString());

        IAtom atomToDelete = canonicalParent.getAtom(vertexToDelete);
        canonicalParent.removeAtomAndConnectedElectronContainers(atomToDelete);
        MoleculeSignature canonicalParentSig = new MoleculeSignature(canonicalParent);
        String canonParentString = canonicalParentSig.toCanonicalString(); 
        boolean canon = canonParentString.equals(parentSignature);
        boolean debug = false;
//        boolean debug = true;
        if (canon) {
            if (debug) {
                System.out.println(canonParentString 
                        + " == " + parentSignature
                        + " -> " + AtomContainerPrinter.toString(child)
                        + " l = " + Arrays.toString(labels)
                        + " " + vertexToDelete);
            }
        } else {
            if (debug) {
                System.out.println(canonParentString 
                        + " != " + parentSignature
                        + " -> " + AtomContainerPrinter.toString(child)
                        + " l = " + Arrays.toString(labels)
                        + " " + vertexToDelete);
            }
        }
        return canon;
    }
    
    private int find(int[] labels, int j) {
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] == j) return i;
        }
        return -1;
    }
    
//    private IAtomContainer reconstruct(String signature) {
//        return MoleculeSignature.fromSignatureString(
//                signature, SilentChemObjectBuilder.getInstance());
//    }

    public void setHCount(int hCount) {
        this.hCount = hCount;
    }

}
