package augment;

/**
 * Provide the next extension for a given position.
 * 
 * @author maclean
 *
 */
public interface ExtensionSource<T, S> {
    
    public S getNext(T position);

}
