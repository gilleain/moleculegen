package group.graph;

import java.util.Set;

import group.AbstractEquitablePartitionRefiner;
import group.IEquitablePartitionRefiner;
import group.Refinable;
import group.invariant.IntegerListInvariant;
import group.invariant.Invariant;


/**
 * Implementation of an abstract equitable partition refiner for simple graphs.
 * 
 * @author maclean
 *
 */
public class GraphEquitablePartitionRefiner extends AbstractEquitablePartitionRefiner 
                                       implements IEquitablePartitionRefiner {
    
  
    
    private final Refinable refinable;
    
    public GraphEquitablePartitionRefiner(Refinable refinable) {
        this.refinable = refinable;
    }

    public int getNumberOfVertices() {
        return refinable.getVertexCount();
    }

    @Override
    public Invariant neighboursInBlock(Set<Integer> block, int vertexIndex) {
        int[] colorCounts = new int[refinable.getMaxConnectivity()];
        
        for (int connectedIndex : refinable.getConnectedIndices(vertexIndex)) {
            if (block.contains(connectedIndex)) {
                int color = refinable.getConnectivity(vertexIndex, connectedIndex);
                colorCounts[color - 1]++;
            }
        }
        return new IntegerListInvariant(colorCounts);
    }

}
