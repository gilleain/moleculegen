package augment;

import java.util.List;

public interface ConstrainedAugmentor<T, S, R> {
        
    public List<ConstrainedAugmentation<T, S, R>> augment(ConstrainedAugmentation<T, S, R> parent);

}
