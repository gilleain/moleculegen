package augment;


/**
 * An augmentation of base T by extension S constrained by R.
 * 
 * @author maclean
 *
 */
public interface ConstrainedAugmentation<T, S, R> {
    
    /**
     * @return the base object that is augmented
     */
    public T getBase();
    
    /**
     * @return the extension to apply to the base object
     */
    public S getExtension();
    
    /**
     * @return the constraints ... TODO
     */
    public R getConstraints();

}
