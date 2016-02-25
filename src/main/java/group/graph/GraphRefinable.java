package group.graph;

import java.util.List;

import group.Refinable;
import model.Graph;

public class GraphRefinable implements Refinable {
    
    private final Graph graph;
    
    /**
     * A convenience lookup table for connections.
     */
    private int[][] connectionTable;
    
    /**
     * A convenience lookup table for edge colors.
     */
    private int[][] edgeColors;
    
    /**
     * The maximum value in the edge color table.
     */
    private int maxEdgeColor;
    
    private final boolean ignoreEdgeColors;
    
    public GraphRefinable(Graph graph) {
        this(graph, false);
    }
        
    public GraphRefinable(Graph graph, boolean ignoreEdgeColors) {    
        this.graph = graph;
        this.ignoreEdgeColors = ignoreEdgeColors;
        setupConnectionTable(graph);
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
                    if (color > maxEdgeColor) {
                        maxEdgeColor = color;
                    }
                }
                i++;
            }
        }
//        System.out.println(Arrays.deepToString(connectionTable));
//        System.out.println(Arrays.deepToString(edgeColors));
    }

    @Override
    public int[] getConnectedIndices(int vertexIndex) {
        return connectionTable[vertexIndex];
    }

    @Override
    public int getVertexCount() {
        return graph.getVertexCount();
    }

    @Override
    public int getMaxConnectivity() {
        return maxEdgeColor;
    }

}
