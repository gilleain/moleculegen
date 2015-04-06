package branch;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * An augmented object.
 * 
 * @author maclean
 *
 */
public interface Augmentation {
    
    /**
     * Check to see if this augmentation is the canonical one.
     * 
     * @return true if it is a canonical augmentation
     */
    public boolean isCanonical();
    
    /**
     * @return the augmented molecule
     */
    public IAtomContainer getAugmentedMolecule();

}
