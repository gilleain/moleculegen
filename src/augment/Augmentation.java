package augment;


/**
 * An augmentation of base T by extension S.
 * 
 * @author maclean
 *
 */
public interface Augmentation<T, S> {
    
    /**
     * @return the base object that is augmented
     */
    public T getBase();
    
    /**
     * @return the extension to apply to the base object
     */
    public S getExtension();

}
