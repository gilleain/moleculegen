package group;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * The connections between bonds in the atom container, expressed as a map
     * between bond indices. So, for each bond, there is a mapping to other bonds
     * it is connected to.
     */
    private Map<Integer, List<Integer>> connectionTable;
    
    public BondEquitablePartitionRefiner(Map<Integer, List<Integer>> connectionTable) {
        this.connectionTable = connectionTable;
    }

    public int neighboursInBlock(Set<Integer> block, int vertexIndex) {
        int neighbours = 0;
        List<Integer> connectedBonds = connectionTable.get(vertexIndex); 
        for (int connected : connectedBonds) {
            if (block.contains(connected)) {
                neighbours++;
            }
        }
        return neighbours;
    }
    
    @Override
    public int getNumberOfVertices() {
        return connectionTable.size();
    }

}
