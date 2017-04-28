package group.molecule;

import java.util.Set;

import group.AbstractEquitablePartitionRefiner;
import group.IEquitablePartitionRefiner;
import group.Refinable;
import group.invariant.IntegerInvariant;
import group.invariant.Invariant;

/**
 * Refiner for atom containers, which refines partitions of the bonds to
 * equitable partitions. Used by the {@link BondDiscretePartitionRefiner}.
 * 
 * @author maclean
 * @cdk.module group
 *
 */
public class BondEquitablePartitionRefiner extends
        AbstractEquitablePartitionRefiner implements IEquitablePartitionRefiner {
    
    /**
     * The bond refinable.
     */
    private final Refinable refinable;
    
    public BondEquitablePartitionRefiner(Refinable refinable) {
        this.refinable = refinable;
    }

    public Invariant neighboursInBlock(Set<Integer> block, int vertexIndex) {
        int neighbours = 0;
        int[] connectedBonds = refinable.getConnectedIndices(vertexIndex); 
        for (int connected : connectedBonds) {
            if (block.contains(connected)) {
                neighbours++;
            }
        }
        return new IntegerInvariant(neighbours);
    }
    
    @Override
    public int getNumberOfVertices() {
        return refinable.getVertexCount();
    }

}
