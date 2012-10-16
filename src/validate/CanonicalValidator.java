package validate;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface CanonicalValidator {
    
    public boolean isCanonical(IAtomContainer atomContainer);

}
