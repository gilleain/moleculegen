package augment;

import java.util.List;

/**
 * Augment an object by some means.
 * 
 * @author maclean
 *
 */
public interface Augmentor<T, S> {
    
    public List<Augmentation<T, S>> augment(Augmentation<T, S> parent); 
}
