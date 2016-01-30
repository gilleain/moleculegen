package augment;

import java.util.List;

public interface Augmentor<T, S, R> {
        
    public List<Augmentation<T, S, R>> augment(Augmentation<T, S, R> parent);

}
