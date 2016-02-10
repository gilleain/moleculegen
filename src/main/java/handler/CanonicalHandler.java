package handler;

/**
 * Handles canonical change events.
 * 
 * @author maclean
 *
 */
public interface CanonicalHandler<T> {
    
    public void handle(T parent, T child, boolean isCanonical);

}
