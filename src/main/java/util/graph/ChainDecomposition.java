package util.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Edge;
import model.Graph;

/**
 * Structure used for finding bridges (or cut-edges) in a graph, based on Jens Schmidt's algorithm.
 * 
 * A chain is either a cycle or path. A graph is made up of a set of these chains connected by 0 
 * or more bridges. To put it another way, any edge not part of a chain is a bridge. 
 * 
 * @author maclean
 *
 */
public class ChainDecomposition {
    
    /**
     * The chains of the molecule that are cycles.
     */
    private List<List<Edge>> cycleChains;
    
    /**
     * The number of edges that are part of a cycle-chain.
     */
    private int cycleChainEdges;
    
    /**
     * The chains of the molecule that are paths.
     */
    private List<List<Edge>> pathChains;
    
    /**
     * The number of edges that are part of a path-chain.
     */
    private int pathChainEdges;
    
    /**
     * The index of the vertices in a pre-order traversal of the depth-first spanning tree.
     */
    private int[] dfi;
    private int dfiIndex;
    
    /**
     * The depth-first index of the parents of each vertex.
     */
    private int[] parentIndex;
    
    /**
     * The non-spanning tree edges, indexed by their low point.
     */
    private Map<Integer, List<Edge>> backEdges;
    
    /**
     * The edges that are not part of a chain are bridges.
     */
    private List<Edge> bridges;
    
    public ChainDecomposition(Graph g) {
        int vsize = g.getVertexCount();
        cycleChains = new ArrayList<List<Edge>>();
        cycleChainEdges = 0;
        
        pathChains = new ArrayList<List<Edge>>();
        pathChainEdges = 0;
        
        backEdges = new HashMap<Integer, List<Edge>>();
        
        dfi = new int[vsize];
        Arrays.fill(dfi, -1);
        
        parentIndex = new int[vsize];
        dfiIndex = 0;
        span(0, -1, g);
        System.out.println("dfi " + Arrays.toString(dfi) + " parent " + Arrays.toString(parentIndex));
        System.out.println(backEdges);
        
        findChains(g);
        findBridges(g);
    }
    
    public List<Edge> getBridges() {
        return bridges;
    }
    
    // slightly wonky implementation here - should not have to pass in graph
    public List<Integer> getCutVertices(Graph mol) {
        List<Integer> cutVertices = new ArrayList<Integer>();
        for (Edge bridge : bridges) {
            cutVertices.add(bridge.getVertex(0));
            cutVertices.add(bridge.getVertex(1));
        }
        int root = getRoot();
        if (root >= 0) { // silently ignoring failed getRoot - not great
            for (List<Edge> cycle : cycleChains) {
                for (Edge bond : cycle) {
                    // if the root vertex is in a cycle, then it is a cut vertex
                    if (bond.contains(root)) {
                        cutVertices.add(root);
                        break;
                    }
                }
            }
        }
        return cutVertices;
    }
    
    private int getRoot() {
        for (int index = 0; index < dfi.length; index++) {
            if (dfi[index] == 0) {
                return index;
            }
        }
        return -1;
    }
    
    private void findBridges(Graph g) {
        int esize = g.getEdgeCount();
        BitSet visitedEdges = new BitSet(esize);
        bridges = new ArrayList<Edge>();
        // if all the edges are covered, there can be no bridges
        // if there are no chains, IAtomContainer is a tree
        int totalChainEdges = cycleChainEdges + pathChainEdges; 
        if (totalChainEdges == esize || totalChainEdges == 0) {
            return;
        } else {
            for (List<Edge> cycle : cycleChains) {
                setVisited(visitedEdges, cycle, g);
            }
            for (List<Edge> path : pathChains) {
                setVisited(visitedEdges, path, g);
            }
        }
        for (int i = visitedEdges.nextClearBit(0); i >= 0 && i < esize; i = visitedEdges.nextClearBit(i + 1)) {
            bridges.add(g.getEdge(i));
        }
    }
    
    private void setVisited(BitSet visitedEdges, List<Edge> chain, Graph g) {
        for (Edge e : chain) {
            int indexInIAtomContainer = g.getEdgeNumber(e);
            visitedEdges.set(indexInIAtomContainer);
        }
    }

    private void span(int vertex, int parent, Graph g) {
        dfi[vertex] = dfiIndex;
        dfiIndex++;
        parentIndex[vertex] = parent;
        for (int neighbour : g.getConnected(vertex)) {
            if (neighbour == parent) continue;
            if (dfi[neighbour] == -1) { // neighbour not visited yet
                span(neighbour, vertex, g); 
            } else {
                Edge backedge = new Edge(neighbour, vertex);
                // assumes that dfi[neighbour] is always lower than dfi[vertex]
                int neighbourDFI = dfi[neighbour];
                List<Edge> edgesForVertex;
                boolean addEdge = true;
                if (backEdges.containsKey(neighbourDFI)) {
                    edgesForVertex = backEdges.get(neighbourDFI);
                    // relies on (v, w) = (w, v)
                    if (edgesForVertex.contains(backedge)) {
                        addEdge = false;
                    }
                } else {
                    edgesForVertex = new ArrayList<Edge>();
                    backEdges.put(neighbourDFI, edgesForVertex);
                }
                    
                if (backEdges.containsKey(dfi[vertex])) {
                    // relies on (v, w) = (w, v)
                    if (backEdges.get(dfi[vertex]).contains(backedge)) {
                        addEdge = false;
                    }
                }
                if (addEdge) {
                    edgesForVertex.add(backedge);
                }
            }
        }
    }
    
    private void findChains(Graph g) {
        int n = g.getVertexCount();
        BitSet visitedVertices = new BitSet(n);
        
        // run through the backedges starting with the lowest
        for (int v : backEdges.keySet()) {
            List<Edge> edges = backEdges.get(v);
            for (Edge e : edges) {
                List<Edge> chain = new ArrayList<Edge>();
                // start by adding the backedge itself
                int a = e.getVertex(0);
                visitedVertices.set(a);
                chain.add(e);
                int current = e.getVertex(1);
                while (current != a && !visitedVertices.get(current)) {
                    visitedVertices.set(current);
                    int next = parentIndex[current];
                    chain.add(new Edge(current, next));
                    current = next;
                }
                if (current == a) {
                    cycleChains.add(chain);
                    cycleChainEdges += chain.size();
                } else {
                    pathChains.add(chain);
                    pathChainEdges += chain.size();
                }
            }
        }
    }
    
    public List<List<Edge>> getCycleChains() {
        return cycleChains;
    }
    
    public int getCycleChainEdgeCount() {
        return cycleChainEdges;
    }
    
    public List<List<Edge>> getPathChains() {
        return pathChains;
    }
    
    public int getPathChainEdgeCount() {
        return pathChainEdges;
    }
}
