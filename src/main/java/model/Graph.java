package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Vertex and edge colored graph.
 * 
 * @author maclean
 *
 */
public class Graph implements Serializable {
    
    /**
     * Generated ID
     */
    private static final long serialVersionUID = 8149801109375825404L;
    
    private final List<Edge> edges;
    
    private final List<String> vertexColors;
    
    private final List<Integer> edgeColors;
    
    public Graph() {
        this.vertexColors = new ArrayList<String>();
        this.edges = new ArrayList<Edge>();
        this.edgeColors = new ArrayList<Integer>();
    }

    public Graph(Graph other) {
        this();
        this.vertexColors.addAll(other.vertexColors);
        for (Edge otherEdge : other.edges) {
            this.edges.add(new Edge(otherEdge));
        }
        this.edgeColors.addAll(other.edgeColors);
    }
    
    public Graph(String acpString) {
        this();
        int gapIndex = acpString.indexOf(' ');
        if (gapIndex == -1) {
            return;    // TODO : raise error
        }
        
        String elementString = acpString.substring(0, gapIndex);
        // skip the atom number, as this is just a visual convenience
        for (int index = 0; index < elementString.length();) {
            String elementSymbol = String.valueOf(elementString.charAt(index));
            vertexColors.add(elementSymbol);
            if (index < 20) {
                index += 2;
            } else {
                index += 3;
            }
        }
        
        String bondString = acpString.substring(gapIndex + 1);
        for (String bondPart : bondString.split(",")) {
            int colonIndex = bondPart.indexOf(':');
            int openBracketIndex = bondPart.indexOf('(');
            int closeBracketIndex = bondPart.indexOf(')');
            int a0 = Integer.parseInt(bondPart.substring(0, colonIndex));
            int a1 = Integer.parseInt(bondPart.substring(colonIndex + 1, openBracketIndex));
            String o = bondPart.substring(openBracketIndex + 1, closeBracketIndex);
            addEdge(a0, a1, Integer.valueOf(o));
        }
    }

    public void addVertex(String vertexColor) {
        this.vertexColors.add(vertexColor);
    }
    
    public void addEdge(int startIndex, int endIndex, int color) {
        edges.add(new Edge(startIndex, endIndex));
        edgeColors.add(color);
    }

    public int getVertexCount() {
        return vertexColors.size();
    }

    public String getVertexColor(int vertexIndex) {
        return vertexColors.get(vertexIndex);
    }

    public List<Integer> getConnected(int vertexIndex) {
        List<Integer> connected = new ArrayList<Integer>();
        for (Edge edge : edges) {
            int adjacent = edge.getAdjacent(vertexIndex);
            if (adjacent != -1) {
                connected.add(adjacent);
            }
        }
        return connected;
    }
    
    public Edge getEdge(int vertexIndex, int otherVertexIndex) {
        for (Edge edge : edges) {
            if (edge.contains(vertexIndex) && edge.contains(otherVertexIndex)) {
                return edge;
            }
        }
        return null;
    }

    public int getEdgeColor(int vertexIndex, int otherVertexIndex) {
        int edgeIndex = getEdgeNumber(getEdge(vertexIndex, otherVertexIndex));
//        System.out.println("looking up " + vertexIndex + " " + otherVertexIndex + " in"
//                + edges + " eindex " + edgeIndex + " in " + edgeColors);
        return edgeColors.get(edgeIndex);
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public Edge getEdge(int index) {
        return edges.get(index);
    }

    public int getConnectedVertexCount(int vertexIndex) {
        int count = 0;
        for (Edge edge : edges) {
            int adjacent = edge.getAdjacent(vertexIndex);
            if (adjacent != -1) {
                count++;
            }
        }
        return count;
    }

    public int getEdgeNumber(Edge other) {
        int index = edges.indexOf(other);
        if (index == -1) {
            for (int edgeIndex = 0; edgeIndex < edges.size(); edgeIndex++) {
                Edge edge = edges.get(edgeIndex);
                if (edge.equals(other)) {
                    return edgeIndex;
                }
            }
            return -1;
        } else {
            return index;
        }
    }

    public List<Edge> edges() {
        return edges;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int vertexIndex = 0; vertexIndex < getVertexCount(); vertexIndex++) {
            sb.append(getVertexColor(vertexIndex)).append(vertexIndex);
        }
        sb.append(" ");
        
        int edgeCounter = 0;
        for (Edge edge : edges()) {
            int pA0 = edge.getVertex(0);
            int pA1 = edge.getVertex(1);
            int o = getEdgeColor(pA0, pA1);
            if (pA0 < pA1) {
                sb.append(pA0 + ":" + pA1 + "(" + o + ")");
            } else {
                sb.append(pA1 + ":" + pA0 + "(" + o + ")");
            }
            if (edgeCounter < getEdgeCount() - 1) {
                sb.append(",");
            }
            edgeCounter++;
        }
        return sb.toString();
    }
}
