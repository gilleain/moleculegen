package canonical;

import augment.Augmentation;

public interface CanonicalChecker<T, S, R> {
    
    public boolean isCanonical(Augmentation<T, S, R> augmentation);

}
