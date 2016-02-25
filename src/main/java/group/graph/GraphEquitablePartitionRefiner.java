package group.graph;

import java.util.Arrays;
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
    
    private class IntegerListInvariant implements Invariant {
        
        private int[] values;
        
        public IntegerListInvariant(int[] values) {
            this.values = values;
        }

        @Override
        public int compareTo(Invariant o) {
            int[] other = ((IntegerListInvariant)o).values;
            for (int index = 0; index < values.length; index++) {
                if (values[index] > other[index]) {
                    return -1;
                } else if (values[index] < other[index]) {
                    return 1;
                } else {
                    continue;
                }
            }
            return 0;
        }
        
        public int hashCode() {
            return Arrays.hashCode(values);
        }
        
        public boolean equals(Object other) {
            return other instanceof IntegerListInvariant
                    && Arrays.equals(values, ((IntegerListInvariant)other).values);
        }
        
        public String toString() {
            return Arrays.toString(values);
        }
        
    }
    
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
        int count = 0;
        
        for (int connectedIndex : refinable.getConnectedIndices(vertexIndex)) {
            if (block.contains(connectedIndex)) {
                int color = refinable.getConnectivity(vertexIndex, connectedIndex);
                colorCounts[color - 1]++;
                count++;
            }
        }
        return new IntegerListInvariant(colorCounts);
//        return new IntegerInvariant(count);
    }

}
