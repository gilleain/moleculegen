package augment;

import java.io.Serializable;
import java.util.List;

public interface Augmentor<T> extends Serializable {
        
    public List<T> augment(T parent);

}
