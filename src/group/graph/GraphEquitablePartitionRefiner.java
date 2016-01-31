package group.graph;

import java.util.Set;

import group.AbstractEquitablePartitionRefiner;
import group.IEquitablePartitionRefiner;
import model.Graph;


/**
 * Implementation of an abstract equitable partition refiner for simple graphs.
 * 
 * @author maclean
 *
 */
public class GraphEquitablePartitionRefiner extends AbstractEquitablePartitionRefiner 
                                       implements IEquitablePartitionRefiner {
    
    private Graph graph;
    
    private final GraphDiscretePartitionRefiner discreteRefiner;
    
    public GraphEquitablePartitionRefiner(Graph graph, GraphDiscretePartitionRefiner discreteRefiner) {
        this.graph = graph;
        this.discreteRefiner = discreteRefiner;
    }

    public int getNumberOfVertices() {
        return graph.getVertexCount();
    }

    @Override
    public int neighboursInBlock(Set<Integer> block, int vertexIndex) {
        int n = 0;
        
        for (int i : discreteRefiner.getConnectedIndices(vertexIndex)) {
            if (block.contains(i)) {
                n++;
            }
        }
        return n;
    }

}
