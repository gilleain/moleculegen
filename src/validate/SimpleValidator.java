package validate;

import generate.AtomSymmetricChildLister;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;

public class SimpleValidator implements MoleculeValidator {
    
    private AtomSymmetricChildLister childLister;
    
    public SimpleValidator(AtomSymmetricChildLister childLister) {
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
        if (vertexToDelete == size) {
            return true;
        } else {
            String parentSignature = childLister.getParentSignature();
            try {
                IAtomContainer canonicalParent = (IAtomContainer) child.clone();
                IAtom atomToDelete = canonicalParent.getAtom(vertexToDelete);
                canonicalParent.removeAtomAndConnectedElectronContainers(atomToDelete);
                MoleculeSignature canonicalParentSig = new MoleculeSignature(canonicalParent);
                return canonicalParentSig.toCanonicalString().equals(parentSignature);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

}
