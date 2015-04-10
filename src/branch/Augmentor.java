package branch;

import java.util.List;

/**
 * Augment an object by some means.
 * 
 * @author maclean
 *
 */
public interface Augmentor {
    
    public List<Augmentation> augment(Augmentation parent); 

}
