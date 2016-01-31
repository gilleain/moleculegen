package augment.constraints;

import java.util.ArrayList;
import java.util.List;

import model.Graph;

public class VertexColorConstraintSource {
    
    private final VertexColorConstraints vertexColorConstraints;
    
    public VertexColorConstraintSource(VertexColorConstraints vertexColorConstraints) {
        this.vertexColorConstraints = vertexColorConstraints;
    }

    public Iterable<Graph> get() {
        List<Graph> list = new ArrayList<Graph>();
        list.add(makeSingleVertexGraph(vertexColorConstraints.iterator().next()));
        return list;
    }
    
    private Graph makeSingleVertexGraph(String symbol) {
        Graph graph = new Graph();
        graph.addVertex(symbol);
        return graph;
    }

}
