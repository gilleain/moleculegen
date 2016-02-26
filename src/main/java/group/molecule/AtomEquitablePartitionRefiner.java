package group.molecule;

import java.util.List;
import java.util.Map;
import java.util.Set;

import group.AbstractEquitablePartitionRefiner;
import group.IEquitablePartitionRefiner;
import group.invariant.IntegerInvariant;
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
    
    /**
     * The bonds in the atom container, expressed as a list of maps from 
     * connected atoms to bond orders. So, for each atom, there is a map from
     * all connected atoms to the bond orders for the bond between them.
     */
    private List<Map<Integer, Integer>> connectionTable;
    
    public AtomEquitablePartitionRefiner(List<Map<Integer, Integer>> connectionTable) {
        this.connectionTable = connectionTable;
    }

    public Invariant neighboursInBlock(Set<Integer> block, int vertexIndex) {
        int neighbours = 0;
        Map<Integer, Integer> connectedOrders = connectionTable.get(vertexIndex); 
        for (int connected : connectedOrders.keySet()) {
            if (block.contains(connected)) {
                neighbours++;
            }
        }
        return new IntegerInvariant(neighbours);
    }
    
    @Override
    public int getNumberOfVertices() {
        return connectionTable.size();
    }

}
