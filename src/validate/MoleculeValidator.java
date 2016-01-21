package validate;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface MoleculeValidator {
    
    public void checkConnectivity(IAtomContainer atomContainer);
    
    public boolean canExtend(IAtomContainer atomContainer);
	
	public boolean isValidMol(IAtomContainer atomContainer, int size);

	public void setHCount(int hCount);
	
    public void setImplicitHydrogens(IAtomContainer parent);

}
