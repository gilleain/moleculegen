package appbranch.canonical;

import appbranch.augment.Augmentation;

public interface CanonicalChecker<T> {
    
    public boolean isCanonical(Augmentation<T> augmentation);

}
