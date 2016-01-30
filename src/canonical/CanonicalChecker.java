package canonical;

import augment.ConstrainedAugmentation;

public interface CanonicalChecker<T, S, R> {
    
    public boolean isCanonical(ConstrainedAugmentation<T, S, R> augmentation);

}
