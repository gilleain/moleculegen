package util.graph;

import java.util.ArrayList;
import java.util.List;

import model.Edge;
import model.Graph;

/**
 * Determines the cut vertices and edges of a molecule. 
 * 
 * @author maclean
 *
 */
public class CutCalculator {
    
    public static List<Integer> getCutEdges(Graph graph) {
        if (isTree(graph)) {
            return getEdgesForTree(graph); 
        } else {
            return getEdgesForCyclic(graph);
        }
     }
    
    public static List<Integer> getCutVertices(Graph graph) {
       if (isTree(graph)) {
           return getVerticesForTree(graph); 
       } else {
           return getVerticesForCyclic(graph);
       }
    }
    
    private static List<Integer> getEdgesForTree(Graph graph) {
        List<Integer> cutEdges = new ArrayList<Integer>();
        List<Integer> leaves = getLeaves(graph);
        for (int bondIndex = 0; bondIndex < graph.getEdgeCount(); bondIndex++) {
            Edge bond = graph.getEdge(bondIndex);
            int a0 = bond.getVertex(0);
            int a1 = bond.getVertex(1);
            if (!leaves.contains(a0) && !leaves.contains(a1)) {
                cutEdges.add(bondIndex);
            }
        }
        return cutEdges;
    }
    
    
    private static List<Integer> getVerticesForTree(Graph graph) {
        List<Integer> cutVertices = new ArrayList<Integer>();
        List<Integer> leaves = getLeaves(graph);
        for (int x = 0; x < graph.getVertexCount(); x++) {
            if (!leaves.contains(x)) {
                cutVertices.add(x);
            }
        }
        return cutVertices;
    }
    
    private static List<Integer> getEdgesForCyclic(Graph graph) {
        List<Integer> cutEdges = new ArrayList<Integer>();
        ChainDecomposition chainDecomposition = new ChainDecomposition(graph);
        for (Edge bond : chainDecomposition.getBridges()) {
            cutEdges.add(graph.getEdgeNumber(bond));
        }
        return cutEdges;
    }
    
    private static List<Integer> getVerticesForCyclic(Graph graph) {
//        List<Integer> cutVertices = new ArrayList<Integer>();
//        List<Integer> leaves = getLeaves(graph);
//        ChainDecomposition chainDecomposition = new ChainDecomposition(graph);
//        List<IBond> bridges = chainDecomposition.getBridges();
//        for (int x = 0; x < graph.getAtomCount(); x++) {
//            if (!leaves.contains(x) && inBridges(x, graph, bridges)) {
//                cutVertices.add(x);
//            }
//        }
//        return cutVertices;
        return new CutVertexCalculator().getCutVertices(graph);
    }
    
    private static List<Integer> getVerticesForCyclicFromCD(Graph graph) {
        return new ChainDecomposition(graph).getCutVertices(graph);
    }
    
    private static boolean isTree(Graph graph) {
        // XXX assumes connected!
        return graph.getVertexCount() - 1 == graph.getEdgeCount(); 
    }
    
    private static boolean inBridges(int x, Graph graph, List<Edge> bridges) {
        for (Edge bridge : bridges) {
            if (bridge.contains(x)) {
                return true;
            }
        }
        return false;
    }
    
    private static List<Integer> getLeaves(Graph graph) {
        List<Integer> leaves = new ArrayList<Integer>();
        for (int x = 0; x < graph.getVertexCount(); x++) {
            if (graph.getConnectedVertexCount(x) == 1) {
                leaves.add(x);
            }
        }
        return leaves;
    }

}
