package group.molecule;

import java.util.Set;

import group.AbstractEquitablePartitionRefiner;
import group.IEquitablePartitionRefiner;
import group.Refinable;
import group.invariant.IntegerListInvariant;
import group.invariant.Invariant;

/**
 * Refiner for atom containers, which refines partitions of the atoms to
 * equitable partitions. Used by the {@link AtomDiscretePartitionRefiner}.
 * 
 * @author maclean
 * @cdk.module group
 *
 */
public class AtomEquitablePartitionRefiner extends
        AbstractEquitablePartitionRefiner implements IEquitablePartitionRefiner {
    
    private Refinable refinable;
    
    public AtomEquitablePartitionRefiner(Refinable refinable) {
        this.refinable = refinable;
    }

    public Invariant neighboursInBlock(Set<Integer> block, int vertexIndex) {
        int[] bondOrderCounts = new int[refinable.getMaxConnectivity()];
        for (int connected : refinable.getConnectedIndices(vertexIndex)) {
            if (block.contains(connected)) {
                int bondOrder = refinable.getConnectivity(vertexIndex, connected);
                bondOrderCounts[bondOrder - 1]++;
            }
        }
        return new IntegerListInvariant(bondOrderCounts);
    }
    
    @Override
    public int getNumberOfVertices() {
        return refinable.getVertexCount();
    }

}
