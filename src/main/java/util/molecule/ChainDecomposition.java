package util.molecule;

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
        
//        System.out.println("dfi " + Arrays.toString(dfi) + " parent " + Arrays.toString(parentIndex));
//        System.out.print("{");
//        for (int index : backEdges.keySet()) {
//            System.out.print(index + "=[");
//            for (IBond bond : backEdges.get(index)) {
//                System.out.print(g.getAtomNumber(bond.getAtom(0)));
//                System.out.print(":");
//                System.out.print(g.getAtomNumber(bond.getAtom(1)));
//                System.out.print(", ");
//            }
//            System.out.print("], ");
//        }
//        System.out.println("}");
        
        findChains(g);
        findBridges(g);
    }
    
    public List<IBond> getBridges() {
        return bridges;
    }
    
    // slightly wonky implementation here - should not have to pass in mol
    public List<IAtom> getCutVertices(IAtomContainer mol) {
        List<IAtom> cutVertices = new ArrayList<IAtom>();
        for (IBond bridge : bridges) {
            cutVertices.add(bridge.getAtom(0));
            cutVertices.add(bridge.getAtom(1));
        }
        int root = getRoot();
        if (root >= 0) { // silently ignoring failed getRoot - not great
            IAtom rootAtom = mol.getAtom(root);
            for (List<IBond> cycle : cycleChains) {
                for (IBond bond : cycle) {
                    // if the root vertex is in a cycle, then it is a cut vertex
                    if (bond.contains(rootAtom)) {
                        cutVertices.add(rootAtom);
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
                int neighbourDFI = dfi[neighbour];
                List<IBond> edgesForVertex;
                boolean addEdge = true;
                if (backEdges.containsKey(neighbourDFI)) {
                    edgesForVertex = backEdges.get(neighbourDFI);
                    // relies on (v, w) = (w, v)
                    if (edgesForVertex.contains(backedge)) {
                        addEdge = false;
                    }
                } else {
                    edgesForVertex = new ArrayList<IBond>();
                    backEdges.put(neighbourDFI, edgesForVertex);
                }
                    
                if (backEdges.containsKey(dfi[vertex])) {
                    // relies on (v, w) = (w, v)
                    if (contains(g, backedge, backEdges.get(dfi[vertex]))) {
                        addEdge = false;
                    }
                }
                if (addEdge) {
                    edgesForVertex.add(backedge);
                }
            }
        }
    }
    
    // UGH
    private boolean contains(IAtomContainer g, IBond query, List<IBond> container) {
        int query0 = g.getAtomNumber(query.getAtom(0));
        int query1 = g.getAtomNumber(query.getAtom(1));
        for (IBond target : container) {
            int target0 = g.getAtomNumber(target.getAtom(0));
            int target1 = g.getAtomNumber(target.getAtom(1));
            if ((query0 == target0 && query1 == target1)
                    || (query0 == target1 && query1 == target0)) {
                return true;
            }
        }
        return false;
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
