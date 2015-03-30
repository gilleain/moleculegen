package branch;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Augment an object by some means.
 * 
 * @author maclean
 *
 */
public interface Augmentor {
    
    public List<Augmentation> augment(IAtomContainer atomContainer); 

}
