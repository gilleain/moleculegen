package augment;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Runs an {@link Augmentor}.
 * 
 * @author maclean
 *
 */
public interface AugmentingGenerator {
    
    public void run();
    
    public void run(IAtomContainer initial);

    public void finish();

}
