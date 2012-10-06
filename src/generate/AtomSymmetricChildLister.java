package generate;

import group.Permutation;
import group.SSPermutationGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.signature.Orbit;

import combinatorics.MultiKSubsetLister;

/**
 * List candidate children of a parent molecule, by connecting a new atom to the existing atoms 
 * through a set of bonds that are minimal in their orbit.
 * 
 * @author maclean
 *
 */
public class AtomSymmetricChildLister implements ChildLister {
    
    /**
     * TODO : this is a very crude method
     */
    private Map<String, Integer> degreeMap;
    
    public AtomSymmetricChildLister() {
        degreeMap = new HashMap<String, Integer>();
        degreeMap.put("C", 4);
        degreeMap.put("O", 3);
    }
    
    public List<IAtomContainer> listChildren(IAtomContainer parent, int currentAtomIndex) {
        int maxDegreeForCurrent = getMaxDegree(parent.getAtom(currentAtomIndex));
        SSPermutationGroup autG = getGroup(parent);
        List<IAtomContainer> children = new ArrayList<IAtomContainer>();
        for (List<Integer> multiset : getMultisets(parent, maxDegreeForCurrent)) {
            int[] bondOrderArray = toIntArray(multiset, currentAtomIndex);
            if (isMinimal(bondOrderArray, autG)) {
                children.add(makeChild(parent, multiset, currentAtomIndex));
            }
        }
        return children;
    }
    
    public IAtomContainer makeChild(
            IAtomContainer parent, List<Integer> multiset, int lastIndex) {
        try {
            IAtomContainer child = (IAtomContainer) parent.clone();
            for (int value : multiset) {
                if (value > 0) {
                    Order order;
                    switch (value) {
                        case 1: order = Order.SINGLE;
                        case 2: order = Order.DOUBLE;
                        case 3: order = Order.TRIPLE;
                        default: order = Order.SINGLE;
                    }
                    child.addBond(value, lastIndex, order);
                }
            }
            return child;
        } catch (CloneNotSupportedException cnse) {
            // TODO
            return null;
        }
    }
    
    public List<List<Integer>> getMultisets(IAtomContainer parent, int maxDegreeForCurrent) {
        // these are the atom indices that can have bonds added
        List<Integer> baseSet = new ArrayList<Integer>();
        for (int index = 0; index < parent.getAtomCount(); index++) {
            if (isUndersaturated(parent, index)) {
                baseSet.add(index);
            }
        }
        
        List<List<Integer>> multisets = new ArrayList<List<Integer>>();
        for (int k = 1; k <= maxDegreeForCurrent; k++) {
            MultiKSubsetLister<Integer> lister = new MultiKSubsetLister<Integer>(k, baseSet);
            for (List<Integer> multiset : lister) {
                multisets.add(multiset);
            }
        }
        return multisets;
    }
    
    public int[] toIntArray(List<Integer> multiset, int size) {
        int[] intArray = new int[size];
        for (int atomIndex : multiset) {
           intArray[atomIndex]++;
        }
        return intArray;
    }

    public boolean isMinimal(int[] bondOrderArray, SSPermutationGroup autG) {
        for (Permutation p : autG.all()) {
            for (int index = 0; index < bondOrderArray.length; index++) {
                if (bondOrderArray[p.get(index)] < bondOrderArray[index]) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isUndersaturated(IAtomContainer parent, int index) {
        IAtom atom = parent.getAtom(index);
        String elementSymbol = atom.getSymbol();
        int maxDegree = degreeMap.get(elementSymbol);
        int degree = 0;
        for (IBond bond : parent.getConnectedBondsList(atom)) {
            degree += bond.getOrder().ordinal();
        }
        return degree < maxDegree;
    }

    public SSPermutationGroup getGroup(IAtomContainer parent) {
        MoleculeSignature molSig = new MoleculeSignature(parent);
        List<Orbit> orbits = molSig.calculateOrbits();
        int size = parent.getAtomCount();
        SSPermutationGroup autG = new SSPermutationGroup(size);
        for (Orbit orbit : orbits) {
            List<Integer> atomIndices = orbit.getAtomIndices();
            if (atomIndices.size() > 1) {
                Permutation p = new Permutation(size);
                int prev = -1;
                for (int index : orbit) {
                    if (prev > -1) {
                        p.set(prev, index);
                    }
                    prev = index;
                }
                p.set(prev, atomIndices.get(0));
                autG.enter(p);
            }
        }
        return autG;
    }

    private int getMaxDegree(IAtom atom) {
        // TODO Auto-generated method stub
        return 0;
    }

}
