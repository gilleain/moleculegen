package group.graph;

import java.util.Set;

import group.AbstractEquitablePartitionRefiner;
import group.IEquitablePartitionRefiner;
import group.Refinable;


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
    public int neighboursInBlock(Set<Integer> block, int vertexIndex) {
        int count = 0;
        
        for (int connectedIndex : refinable.getConnectedIndices(vertexIndex)) {
            if (block.contains(connectedIndex)) {
                count++;
            }
        }
        return count;
    }

}
