package model;

import java.io.Serializable;

public class Edge implements Serializable {
    
    /**
     * Generated ID
     */
    private static final long serialVersionUID = -5476736566377239477L;

    private final int vertexZero;
    
    private final int vertexOne;
    
    public Edge(int vertexZero, int vertexOne) {
        this.vertexZero = vertexZero;
        this.vertexOne = vertexOne;
    }
    
    public Edge(Edge other) {
        this.vertexZero = other.vertexZero;
        this.vertexOne = other.vertexOne;
    }

    public int getVertex(int vertexIndex) {
        return vertexIndex == 0? vertexZero : vertexOne;
    }

    public boolean contains(int vertex) {
        return vertexZero == vertex || vertexOne == vertex;
    }
    
    public int getAdjacent(int vertex) {
        if (vertex == vertexZero) {
            return vertexOne;
        } else if (vertex == vertexOne) {
            return vertexZero;
        } else {
            return -1;
        }
    }
    
    public String toString() {
        return "(" + vertexZero + ", " + vertexOne + ")";
     }

}
