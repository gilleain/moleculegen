package augment;

import java.io.Serializable;

/**
 * An augmentation of base T by extension S constrained by R.
 * 
 * @author maclean
 *
 */
public interface Augmentation<T> extends Serializable {
    
    /**
     * @return the base object that is augmented
     */
    public T getAugmentedObject();

}
