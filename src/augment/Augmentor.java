package augment;

import java.util.List;

public interface Augmentor<T> {
        
    public List<T> augment(T parent);

}
