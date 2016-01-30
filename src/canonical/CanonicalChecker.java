package canonical;

import augment.Augmentation;

public interface CanonicalChecker<T> {
    
    public boolean isCanonical(Augmentation<T> augmentation);

}
