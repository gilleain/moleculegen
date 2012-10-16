package validate;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface MoleculeValidator {
	
	public boolean isValidMol(IAtomContainer atomContainer, int size);

	public void setHCount(int hCount);

}
