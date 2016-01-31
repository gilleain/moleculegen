package augment;


/**
 * An augmentation of base T by extension S constrained by R.
 * 
 * @author maclean
 *
 */
public interface Augmentation<T> {
    
    /**
     * @return the base object that is augmented
     */
    public T getAugmentedObject();

}
