package augment;

import java.io.Serializable;

public interface CanonicalChecker<T> extends Serializable {
    
    public boolean isCanonical(T t);

}
