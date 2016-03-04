package util.molecule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Determines the cut vertices and edges of a molecule. 
 * 
 * @author maclean
 *
 */
public class CutCalculator {
    
    public static Set<Integer> getCutEdges(IAtomContainer graph) {
        if (isTree(graph)) {
            return getEdgesForTree(graph); 
        } else {
            return getEdgesForCyclic(graph);
        }
     }
    
    public static Set<Integer> getCutVertices(IAtomContainer graph) {
       if (isTree(graph)) {
           return getVerticesForTree(graph); 
       } else {
           return  new HashSet<>(getVerticesForCyclic(graph));
       }
    }
    
    private static Set<Integer> getEdgesForTree(IAtomContainer graph) {
        Set<Integer> cutEdges = new HashSet<>();
        Set<Integer> leaves = new HashSet<>(getLeaves(graph));
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
    
    
    private static Set<Integer> getVerticesForTree(IAtomContainer graph) {
        Set<Integer> cutVertices = new HashSet<Integer>();
        Set<Integer> leaves = new HashSet<Integer>(getLeaves(graph));
        for (int x = 0; x < graph.getAtomCount(); x++) {
            if (!leaves.contains(x)) {
                cutVertices.add(x);
            }
        }
        return cutVertices;
    }
    
    private static Set<Integer> getEdgesForCyclic(IAtomContainer graph) {
        Set<Integer> cutEdges = new HashSet<Integer>();
        ChainDecomposition chainDecomposition = new ChainDecomposition(graph);
        for (IBond bond : chainDecomposition.getBridges()) {
            // FIXME blech!
            IBond actualBond = graph.getBond(bond.getAtom(0), bond.getAtom(1));
            cutEdges.add(graph.getBondNumber(actualBond));
        }
        return cutEdges;
    }
    
    private static List<Integer> getVerticesForCyclic(IAtomContainer graph) {
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
    
    private static List<Integer> getVerticesForCyclicFromCD(IAtomContainer graph) {
        List<IAtom> cutVertexAtoms =
                new ChainDecomposition(graph).getCutVertices(graph);
        List<Integer> cutVertices = new ArrayList<Integer>();
        for (IAtom atom : cutVertexAtoms) {
            cutVertices.add(graph.getAtomNumber(atom));
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
