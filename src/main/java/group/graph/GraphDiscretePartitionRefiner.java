package group.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import group.AbstractDiscretePartitionRefiner;
import group.Partition;
import group.Permutation;
import group.PermutationGroup;
import model.Graph;


/**
 * Test implementation of a discrete partition refiner for simple graphs.
 * 
 * @author maclean
 *
 */
public class GraphDiscretePartitionRefiner extends AbstractDiscretePartitionRefiner {
    
    private boolean ignoreEdgeColors;
    
    private boolean ignoreVertexColors;
    
    /**
     * A convenience lookup table for connections.
     */
    private int[][] connectionTable;
    
    /**
     * A convenience lookup table for edge colors.
     */
    private int[][] edgeColors;
    
    /**
     * The vertex colors
     */
    private Partition colors;
    
    
    public GraphDiscretePartitionRefiner() {
    	this(false, false);
    }
    
    public GraphDiscretePartitionRefiner(boolean ignoreVertexColors, boolean ignoreEdgeColors) {
    	this.ignoreVertexColors = ignoreVertexColors;
    	this.ignoreEdgeColors = ignoreEdgeColors;
    }
    
    public int[] getConnectedIndices(int vertexIndex) {
        return connectionTable[vertexIndex];
    }
    
    public Partition getInitialPartition(Graph graph) {
        if (ignoreVertexColors) {
            int n = graph.getVertexCount();
            return Partition.unit(n);
        }
        
        if (connectionTable == null) {
            setupConnectionTable(graph);
        }
        
        Map<String, SortedSet<Integer>> cellMap = 
                new HashMap<String, SortedSet<Integer>>();
        int numberOfVertices = graph.getVertexCount();
        for (int vertexIndex = 0; vertexIndex < numberOfVertices; vertexIndex++) {
            String color = graph.getVertexColor(vertexIndex);
            SortedSet<Integer> cell;
            if (cellMap.containsKey(color)) {
                cell = cellMap.get(color);
            } else {
                cell = new TreeSet<Integer>();
                cellMap.put(color, cell);
            }
            cell.add(vertexIndex);
        }
        
        List<String> colors = new ArrayList<String>(cellMap.keySet());
        Collections.sort(colors);
        
        Partition colorPartition = new Partition();
        for (String key : colors) {
            SortedSet<Integer> cell = cellMap.get(key);
            colorPartition.addCell(cell);
        }
        
        return colorPartition;
    }
    
    @Override
    public boolean colorsPreserved(Permutation p) {
        if (colors == null) {
            return true;    // XXX
        } else {
            for (int i = 0; i < getVertexCount(); i++) {
                if (inSameCell(i, p.get(i), colors)) {
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        }
    }
    
    // TODO : make this a partition method
    private boolean inSameCell(int i, int j, Partition p) {
        for (int c = 0; c < p.size(); c++) {
            SortedSet<Integer> cell = p.getCell(c); 
            if (cell.contains(i) && cell.contains(j)) {
                return true;
            }
        }
        return false;
    }

    
    public void setup(Graph graph) {
    	if (connectionTable == null) {
    		setupConnectionTable(graph);
    	}
        int n = graph.getVertexCount();
        PermutationGroup group = new PermutationGroup(new Permutation(n));
        super.setup(group, new GraphEquitablePartitionRefiner(graph, this));
    }
    
    private void setup(Graph graph, PermutationGroup group) {
    	setupConnectionTable(graph);
    	super.setup(group, new GraphEquitablePartitionRefiner(graph, this));
    }

    public boolean isCanonical(Graph graph) {
        return isCanonical(graph, getInitialPartition(graph));
    }
    
    public void refine(Graph graph) {
        refine(graph, getInitialPartition(graph));
    }
    
    public void refine(Graph graph, Partition partition) {
        setup(graph);
        super.refine(partition);
    }
    
    public boolean isCanonical(Graph graph, Partition partition) {
        setup(graph);
        refine(partition);
        return isCanonical();
    }
    
    public PermutationGroup getAutomorphismGroup(Graph graph, Partition initialPartiton) {
        setup(graph);
        refine(initialPartiton);
        return getGroup();
    }
    
    public PermutationGroup getAutomorphismGroup(Graph graph) {
        setup(graph);
        Partition initial;
        if (ignoreVertexColors) {
            int n = getVertexCount();
            initial = Partition.unit(n);
        } else {
            initial = getElementPartition(graph);
        }
        super.refine(initial);
        return super.getGroup();
    }
    
    public PermutationGroup getAutomorphismGroup(Graph graph, PermutationGroup group) {
        setup(graph, group);
        refine(Partition.unit(getVertexCount()));
        return getGroup();
    }
    
    /**
     * Get the vertex color partition from a graph, which is simply a list
     * of sets of vertex indices where all vertices in one set have the same color.
     * 
     * So for vertices C0,N1,C2,P3,C4,N5 the partition would be [{0, 2, 4}, {1, 5}, {3}]
     * with cells for colors C, N, and P.
     *  
     * @param graph the graph to get vertex colors from
     * @return a partition of the vertex indices based on the colors
     */
    public Partition getElementPartition(Graph graph) {
        if (connectionTable == null) {
            setupConnectionTable(graph);
        }
        
        Map<String, SortedSet<Integer>> cellMap = new HashMap<String, SortedSet<Integer>>();
        int numberOfAtoms = graph.getVertexCount(); 
        for (int index = 0; index < numberOfAtoms; index++) {
            if (index >= 0) {
                String symbol = graph.getVertexColor(index);
                SortedSet<Integer> cell;
                if (cellMap.containsKey(symbol)) {
                    cell = cellMap.get(symbol);
                } else {
                    cell = new TreeSet<Integer>();
                    cellMap.put(symbol, cell);
                }
                cell.add(index);
            }
        }
        
        List<String> atomSymbols = new ArrayList<String>(cellMap.keySet());
        Collections.sort(atomSymbols);
        
        Partition elementPartition = new Partition();
        for (String key : atomSymbols) {
            SortedSet<Integer> cell = cellMap.get(key);
            elementPartition.addCell(cell);
        }
        
//        this.colors = elementPartition;
        
        return elementPartition;
    }
    
    @Override
    public int getVertexCount() {
        return connectionTable.length;
    }
    
    private void setupConnectionTable(Graph graph) {
    	int vertexCount = graph.getVertexCount();
        connectionTable = new int[vertexCount][];
        if (!ignoreEdgeColors) {
            edgeColors = new int[vertexCount][];
        }
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
        	List<Integer> connected = graph.getConnected(vertexIndex);
            int numConnVertices = connected.size();
            connectionTable[vertexIndex] = new int[numConnVertices];
            if (!ignoreEdgeColors) {
                edgeColors[vertexIndex] = new int[numConnVertices];
            }
            int i = 0;
            for (int connectedVertex : connected) {
                connectionTable[vertexIndex][i] = connectedVertex;
                if (!ignoreEdgeColors) {
                    int color = graph.getEdgeColor(vertexIndex, connectedVertex);
                    edgeColors[vertexIndex][i] = color;
                }
                i++;
            }
        }
    }
    
    public Map<Integer, List<Integer>> makeCompactConnectionTable(Graph graph) {
        List<List<Integer>> table = new ArrayList<List<Integer>>();
        int tableIndex = 0;
        int count = graph.getVertexCount();
        int[] indexMap = new int[count];
        for (int i = 0; i < count; i++) {
            List<Integer> connected = graph.getConnected(i);
            if (connected != null && connected.size() > 0) {
                table.add(connected);
                indexMap[i] = tableIndex;
                tableIndex++;
            }
        }
        Map<Integer, List<Integer>> shortTable =  new HashMap<Integer, List<Integer>>();
        for (int i = 0; i < table.size(); i++) {
            List<Integer> originalConnections = table.get(i);
            List<Integer> mappedConnections = new ArrayList<Integer>();
            for (int j : originalConnections) {
                mappedConnections.add(indexMap[j]);
            }
            shortTable.put(i, mappedConnections);
        }
        return shortTable;
    }

    @Override
    public int getConnectivity(int vertexI, int vertexJ) {
    	 int indexInRow;
         int maxRowIndex = connectionTable[vertexI].length;
         for (indexInRow = 0; indexInRow < maxRowIndex; indexInRow++) {
             if (connectionTable[vertexI][indexInRow] == vertexJ) {
                 break;
             }
         }
         if (ignoreEdgeColors) {
             if (indexInRow < maxRowIndex) {
                 return 1;
             } else {
                 return 0;
             }
         } else {
             if (indexInRow < maxRowIndex) {
                 return edgeColors[vertexI][indexInRow];
             } else {
                 return 0;
             }
         }
     }
}
