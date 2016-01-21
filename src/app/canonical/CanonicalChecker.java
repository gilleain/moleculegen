package app.canonical;

import app.augment.Augmentation;

public interface CanonicalChecker<T, S> {
    
    public boolean isCanonical(Augmentation<T, S> augmentation);

}
