package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.Bond;

/**
 * Structure used for finding bridges (or cut-edges) in a molecule, based on Jens Schmidt's algorithm.
 * 
 * A chain is either a cycle or path. A molecule is made up of a set of these chains connected by 0 
 * or more bridges. To put it another way, any edge not part of a chain is a bridge. 
 * 
 * @author maclean
 *
 */
public class ChainDecomposition {
    
    /**
     * The chains of the molecule that are cycles.
     */
    private List<List<IBond>> cycleChains;
    
    /**
     * The number of edges that are part of a cycle-chain.
     */
    private int cycleChainEdges;
    
    /**
     * The chains of the molecule that are paths.
     */
    private List<List<IBond>> pathChains;
    
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
    private Map<Integer, List<IBond>> backEdges;
    
    /**
     * The edges that are not part of a chain are bridges.
     */
    private List<IBond> bridges;
    
    public ChainDecomposition(IAtomContainer g) {
        int vsize = g.getAtomCount();
        cycleChains = new ArrayList<List<IBond>>();
        cycleChainEdges = 0;
        
        pathChains = new ArrayList<List<IBond>>();
        pathChainEdges = 0;
        
        backEdges = new HashMap<Integer, List<IBond>>();
        
        dfi = new int[vsize];
        Arrays.fill(dfi, -1);
        
        parentIndex = new int[vsize];
        dfiIndex = 0;
        span(0, -1, g);
        
        findChains(g);
        findBridges(g);
    }
    
    public List<IBond> getBridges() {
        return bridges;
    }
    
    private void findBridges(IAtomContainer g) {
        int esize = g.getBondCount();
        BitSet visitedEdges = new BitSet(esize);
        bridges = new ArrayList<IBond>();
        // if all the edges are covered, there can be no bridges
        // if there are no chains, IAtomContainer is a tree
        int totalChainEdges = cycleChainEdges + pathChainEdges; 
        if (totalChainEdges == esize || totalChainEdges == 0) {
            return;
        } else {
            for (List<IBond> cycle : cycleChains) {
                setVisited(visitedEdges, cycle, g);
            }
            for (List<IBond> path : pathChains) {
                setVisited(visitedEdges, path, g);
            }
        }
        for (int i = visitedEdges.nextClearBit(0); i >= 0 && i < esize; i = visitedEdges.nextClearBit(i + 1)) {
            bridges.add(g.getBond(i));
        }
    }
    
    private void setVisited(BitSet visitedEdges, List<IBond> chain, IAtomContainer g) {
        for (IBond e : chain) {
            int indexInIAtomContainer = g.getBondNumber(e.getAtom(0), e.getAtom(1));
            visitedEdges.set(indexInIAtomContainer);
        }
    }

    private void span(int vertex, int parent, IAtomContainer g) {
        dfi[vertex] = dfiIndex;
        dfiIndex++;
        parentIndex[vertex] = parent;
        for (IAtom neighbourAtom : g.getConnectedAtomsList(g.getAtom(vertex))) {
            int neighbour = g.getAtomNumber(neighbourAtom);
            if (neighbour == parent) continue;
            if (dfi[neighbour] == -1) { // neighbour not visited yet
                span(neighbour, vertex, g); 
            } else {
                IBond backedge = new Bond(g.getAtom(neighbour), g.getAtom(vertex));
                // assumes that dfi[neighbour] is always lower than dfi[vertex]
                int key = dfi[neighbour];
                List<IBond> edgesForVertex;
                boolean addEdge = true;
                if (backEdges.containsKey(key)) {
                    edgesForVertex = backEdges.get(key);
                    // relies on (v, w) = (w, v)
                    if (edgesForVertex.contains(backedge)) {
                        addEdge = false;
                    }
                } else {
                    edgesForVertex = new ArrayList<IBond>();
                    backEdges.put(key, edgesForVertex);
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
    
    private void findChains(IAtomContainer g) {
        int n = g.getAtomCount();
        BitSet visitedVertices = new BitSet(n);
        
        // run through the backedges starting with the lowest
        for (int v : backEdges.keySet()) {
            List<IBond> edges = backEdges.get(v);
            for (IBond e : edges) {
                List<IBond> chain = new ArrayList<IBond>();
                // start by adding the backedge itself
                int a = g.getAtomNumber(e.getAtom(0));
                visitedVertices.set(a);
                chain.add(new Bond(e.getAtom(0), e.getAtom(1)));
                int current = g.getAtomNumber(e.getAtom(1));
                while (current != a && !visitedVertices.get(current)) {
                    visitedVertices.set(current);
                    int next = parentIndex[current];
                    chain.add(new Bond(g.getAtom(current), g.getAtom(next)));
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
    
    public List<List<IBond>> getCycleChains() {
        return cycleChains;
    }
    
    public int getCycleChainEdgeCount() {
        return cycleChainEdges;
    }
    
    public List<List<IBond>> getPathChains() {
        return pathChains;
    }
    
    public int getPathChainEdgeCount() {
        return pathChainEdges;
    }
}
