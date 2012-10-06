package validate;

import org.openscience.cdk.interfaces.IAtomContainer;

public class SimpleValidator implements MoleculeValidator {

    @Override
    public boolean isValidMol(IAtomContainer atomContainer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isConnected(IAtomContainer atomContainer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isCanonical(IAtomContainer parent, IAtomContainer child) {
        // TODO Auto-generated method stub
        return false;
    }

}
