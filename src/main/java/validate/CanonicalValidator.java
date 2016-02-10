package validate;

import java.io.Serializable;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface CanonicalValidator extends Serializable {
    
    public boolean isCanonical(IAtomContainer atomContainer);

}
