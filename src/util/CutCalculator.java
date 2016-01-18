package util;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Determines the cut vertices and edges of a molecule. 
 * 
 * @author maclean
 *
 */
public class CutCalculator {
    
    public static List<Integer> getCutEdges(IAtomContainer graph) {
        if (isTree(graph)) {
            return getEdgesForTree(graph); 
        } else {
            return getEdgesForCyclic(graph);
        }
     }
    
    public static List<Integer> getCutVertices(IAtomContainer graph) {
       if (isTree(graph)) {
           return getVerticesForTree(graph); 
       } else {
           return getVerticesForCyclic(graph);
       }
    }
    
    private static List<Integer> getEdgesForTree(IAtomContainer graph) {
        List<Integer> cutEdges = new ArrayList<Integer>();
        List<Integer> leaves = getLeaves(graph);
        for (int bondIndex = 0; bondIndex < graph.getBondCount(); bondIndex++) {
            IBond bond = graph.getBond(bondIndex);
            int a0 = graph.getAtomNumber(bond.getAtom(0));
            int a1 = graph.getAtomNumber(bond.getAtom(1));
            if (!leaves.contains(a0) && !leaves.contains(a1)) {
                cutEdges.add(bondIndex);
            }
        }
        return cutEdges;
    }
    
    
    private static List<Integer> getVerticesForTree(IAtomContainer graph) {
        List<Integer> cutVertices = new ArrayList<Integer>();
        List<Integer> leaves = getLeaves(graph);
        for (int x = 0; x < graph.getAtomCount(); x++) {
            if (!leaves.contains(x)) {
                cutVertices.add(x);
            }
        }
        return cutVertices;
    }
    
    private static List<Integer> getEdgesForCyclic(IAtomContainer graph) {
        List<Integer> cutEdges = new ArrayList<Integer>();
        ChainDecomposition chainDecomposition = new ChainDecomposition(graph);
        for (IBond bond : chainDecomposition.getBridges()) {
            // FIXME blech!
            IBond actualBond = graph.getBond(bond.getAtom(0), bond.getAtom(1));
            cutEdges.add(graph.getBondNumber(actualBond));
        }
        return cutEdges;
    }
    
    private static List<Integer> getVerticesForCyclic(IAtomContainer graph) {
        List<Integer> cutVertices = new ArrayList<Integer>();
        List<Integer> leaves = getLeaves(graph);
        ChainDecomposition chainDecomposition = new ChainDecomposition(graph);
        List<IBond> bridges = chainDecomposition.getBridges();
        for (int x = 0; x < graph.getAtomCount(); x++) {
            if (!leaves.contains(x) && inBridges(x, graph, bridges)) {
                cutVertices.add(x);
            }
        }
        return cutVertices;
    }
    
    private static boolean isTree(IAtomContainer graph) {
        // XXX assumes connected!
        return graph.getAtomCount() - 1 == graph.getBondCount(); 
    }
    
    private static boolean inBridges(int x, IAtomContainer graph, List<IBond> bridges) {
        for (IBond bridge : bridges) {
            if (bridge.contains(graph.getAtom(x))) {
                return true;
            }
        }
        return false;
    }
    
    private static List<Integer> getLeaves(IAtomContainer graph) {
        List<Integer> leaves = new ArrayList<Integer>();
        for (int x = 0; x < graph.getAtomCount(); x++) {
            if (graph.getConnectedAtomsCount(graph.getAtom(x)) == 1) {
                leaves.add(x);
            }
        }
        return leaves;
    }

}
