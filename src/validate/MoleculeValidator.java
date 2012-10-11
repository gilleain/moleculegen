package validate;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface MoleculeValidator {
	
	public boolean isValidMol(IAtomContainer atomContainer, int size);

	public boolean isCanonical(IAtomContainer parent, IAtomContainer child, String parentCertificate);
	
	public void setHCount(int hCount);

}
