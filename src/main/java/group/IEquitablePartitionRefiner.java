package group;

/**
 * Interface that the discrete partition refiner uses to interect with 
 * a particular implementation of an equitable partition refiner. 
 * 
 * @author maclean
 * @cdk.module group
 */
public interface IEquitablePartitionRefiner {
    
    /**
     * Refines the coarse partition into an equitable partition that is at least
     * as fine, or finer.
     * 
     * @param coarse the partition to refine
     * @return a partition that is at least as fine, or finer
     */
    public Partition refine(Partition coarse);

}
