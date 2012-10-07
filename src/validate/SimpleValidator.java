package validate;

import java.util.Arrays;

import generate.SignatureChildLister;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.signature.MoleculeSignature;

import test.AtomContainerPrinter;

public class SimpleValidator implements MoleculeValidator {
    
    private SignatureChildLister childLister;
    
    public SimpleValidator(SignatureChildLister childLister) {
        this.childLister = childLister;
    }

    @Override
    public boolean isValidMol(IAtomContainer atomContainer, int size) {
        // TODO!
        return atomContainer.getAtomCount() == size;
    }

    @Override
    public boolean isCanonical(IAtomContainer parent, IAtomContainer child) {
        MoleculeSignature molSig = new MoleculeSignature(child);
        int[] labels = molSig.getCanonicalLabels();
        int size = labels.length - 1;
        int vertexToDelete = labels[size];
//        int vertexToDelete = find(labels, size);
//        if (vertexToDelete == size) {
//            return true;
//        } else {
//            String parentSignature = childLister.getParentSignature();
        String parentSignature = new MoleculeSignature(parent).toCanonicalString();
        String childSignature = new MoleculeSignature(child).toCanonicalString();
//            try {
//                IAtomContainer canonicalParent = (IAtomContainer) child.clone();
                IAtomContainer canonicalParent = reconstruct(childSignature);
                IAtom atomToDelete = canonicalParent.getAtom(vertexToDelete);
                canonicalParent.removeAtomAndConnectedElectronContainers(atomToDelete);
                MoleculeSignature canonicalParentSig = new MoleculeSignature(canonicalParent);
                String canonParentString = canonicalParentSig.toCanonicalString(); 
                boolean canon = canonParentString.equals(parentSignature);
//                if (canon) {
//                    System.out.println(canonParentString 
//                            + " == " + parentSignature
//                            + " -> " + AtomContainerPrinter.toString(child)
//                            + " l = " + Arrays.toString(labels));
//                } else {
//                    System.out.println(canonParentString 
//                            + " != " + parentSignature
//                            + " -> " + AtomContainerPrinter.toString(child)
//                            + " l = " + Arrays.toString(labels));
//                }
                return canon;
//            } catch (CloneNotSupportedException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
    }
    
    private IAtomContainer reconstruct(String signature) {
        return MoleculeSignature.fromSignatureString(
                signature, DefaultChemObjectBuilder.getInstance());
    }
    
    private int find(int[] labels, int j) {
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] == j) return i;
        }
        return -1;
    }

}
