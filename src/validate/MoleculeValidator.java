package validate;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface MoleculeValidator {
	
	public boolean isValidMol(IAtomContainer atomContainer);

	public boolean isConnected(IAtomContainer atomContainer);

	public boolean isCanonical(IAtomContainer parent, IAtomContainer child);

}
