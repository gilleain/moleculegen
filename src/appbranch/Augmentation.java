package appbranch;


/**
 * An augmented object.
 * 
 * @author maclean
 *
 */
public interface Augmentation<T> {
    
    /**
     * Check to see if this augmentation is the canonical one.
     * 
     * @return true if it is a canonical augmentation
     */
    public boolean isCanonical();
    
    /**
     * @return the augmented object
     */
    public T getAugmentedMolecule();

}
