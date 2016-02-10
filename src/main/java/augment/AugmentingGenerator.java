package augment;

import java.io.Serializable;

import handler.Handler;

/**
 * Runs an {@link Augmentor}.
 * 
 * @author maclean
 *
 */
public interface AugmentingGenerator<T> extends Serializable {
    
    public void run();
    
    public void run(T initial);

    public void finish();
    
    public Handler<T> getHandler();

}
