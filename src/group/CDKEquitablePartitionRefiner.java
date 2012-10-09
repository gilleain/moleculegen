package group;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Refiner for atom containers, which refines partitions of the atoms to
 * equitable partitions. Used by the {@link CDKDiscretePartitionRefiner}.
 * 
 * @author maclean
 * @cdk.module group
 *
 */
public class CDKEquitablePartitionRefiner extends
        AbstractEquitablePartitionRefiner implements IEquitablePartitionRefiner {
    
    /**
     * The bonds in the atom container, expressed as a list of maps from 
     * connected atoms to bond orders. So, for each atom, there is a map from
     * all connected atoms to the bond orders for the bond between them.
     */
    private List<Map<Integer, Integer>> connectionTable;
    
    private boolean useBondOrder = true;
    
    private boolean useAtomSymbol = true;
    
    public CDKEquitablePartitionRefiner(List<Map<Integer, Integer>> connectionTable) {
        this(connectionTable, true);
    }
    
    public CDKEquitablePartitionRefiner(
            List<Map<Integer, Integer>> connectionTable, boolean useBondOrder) {
        this.connectionTable = connectionTable;
        this.useBondOrder = useBondOrder;
    }

    public int neighboursInBlock(Set<Integer> block, int vertexIndex) {
        int neighbours = 0;
        Map<Integer, Integer> connectedOrders = connectionTable.get(vertexIndex); 
        for (int connected : connectedOrders.keySet()) {
            if (block.contains(connected) && (atomSymbolOk(vertexIndex, connected))) {
                if (useBondOrder) {
                    neighbours += connectedOrders.get(connected);
                } else {
                    neighbours += 1;
                }
            }
        }
        return neighbours;
    }
    
    private boolean atomSymbolOk(int vertexIndex, int connected) {
        if (useAtomSymbol) {
            return true;   // TODO
        } else {
            return true;
        }
    }
    
    @Override
    public int getNumberOfVertices() {
        return connectionTable.size();
    }

}
