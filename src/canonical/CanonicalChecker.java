package canonical;

import augment.Augmentation;

public interface CanonicalChecker<T, S> {
    
    public boolean isCanonical(Augmentation<T, S> augmentation);

}
