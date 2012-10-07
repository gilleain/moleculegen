package generate;

import group.Permutation;
import group.SSPermutationGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.Atom;
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
    
    /**
     * An optimisation : since the signature of the parent has to be calculated to
     * make the group, we calculate it here to be used in the validator for the 
     * canonical checking step 
     */
    private String parentSignature;
    
    /**
     * The elements (in order) used to make this molecule.
     */
    private List<String> elementSymbols;
    
    public AtomSymmetricChildLister() {
        degreeMap = new HashMap<String, Integer>();
        degreeMap.put("C", 4);
        degreeMap.put("O", 3);
    }
    
    /**
     * Convenience constructor for testing.
     * 
     * @param elementString
     */
    public AtomSymmetricChildLister(String elementString) {
        this();
        setElementString(elementString);
    }
    
    public void setElementString(String elementString) {
        elementSymbols = new ArrayList<String>();
        for (int i = 0; i < elementString.length(); i++) {
            elementSymbols.add(String.valueOf(elementString.charAt(i)));
        }
    }
    
    public void setElementSymbols(List<String> elementSymbols) {
        this.elementSymbols = elementSymbols;
    }
    
    public String getParentSignature() {
        return parentSignature;
    }
    
    public List<IAtomContainer> listChildren(IAtomContainer parent, int currentAtomIndex) {
        int maxDegreeForCurrent = degreeMap.get(elementSymbols.get(currentAtomIndex));
        SSPermutationGroup autG = getGroup(parent);
        List<IAtomContainer> children = new ArrayList<IAtomContainer>();
        int maxMultisetSize = Math.min(currentAtomIndex, maxDegreeForCurrent);
//        System.out.println("mms " + maxMultisetSize);
        for (List<Integer> multiset : getMultisets(parent, maxMultisetSize)) {
            int[] bondOrderArray = toIntArray(multiset, maxMultisetSize);
            if (bondOrderArray != null && isMinimal(bondOrderArray, autG)) {
//                System.out.println(Arrays.toString(bondOrderArray));
                children.add(makeChild(parent, bondOrderArray, currentAtomIndex));
            }
        }
        return children;
    }
    
    public IAtomContainer makeChild(
            IAtomContainer parent, int[] bondOrderArr, int lastIndex) {
        try {
            IAtomContainer child = (IAtomContainer) parent.clone();
            child.addAtom(new Atom(elementSymbols.get(lastIndex)));
            for (int index = 0; index < bondOrderArr.length; index++) {
                int value = bondOrderArr[index];
                if (value > 0) {
                    Order order;
                    switch (value) {
                        case 1: order = Order.SINGLE; break;
                        case 2: order = Order.DOUBLE; break;
                        case 3: order = Order.TRIPLE; break;
                        default: order = Order.SINGLE;
                    }
                    child.addBond(index, lastIndex, order);
                }
            }
//            System.out.println(Arrays.toString(bondOrderArr) + "\t" 
//                    + AtomContainerPrinter.toString(child));
            return child;
        } catch (CloneNotSupportedException cnse) {
            // TODO
            return null;
        }
    }
    
    public List<List<Integer>> getMultisets(IAtomContainer parent, int max) {
        // these are the atom indices that can have bonds added
        List<Integer> baseSet = new ArrayList<Integer>();
        for (int index = 0; index < parent.getAtomCount(); index++) {
            if (isUndersaturated(parent, index)) {
                baseSet.add(index);
            }
        }
        
        List<List<Integer>> multisets = new ArrayList<List<Integer>>();
        for (int k = 1; k <= max; k++) {
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
            if (atomIndex >= size) return null; // XXX
            intArray[atomIndex]++;
        }
        return intArray;
    }
    
    public boolean isMinimal(int[] bondOrderArray, SSPermutationGroup autG) {
        String oStr = Arrays.toString(bondOrderArray);
        for (Permutation p : autG.all()) {
//            System.out.println("comparing " + oStr + " and " + p + " of " + Arrays.toString(bondOrderArray));
            String pStr = Arrays.toString(permute(bondOrderArray, p));
            if (oStr.compareTo(pStr) < 0) {
                return false;
            }
        }
        return true;
    }
    
    private int[] permute(int[] a, Permutation p) {
        int[] pA = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            pA[p.get(i)] = a[i];
        }
        return pA;
    }

    // XXX - alternative test for minimality that tests without stringifying the
    // bondOrderArrays, but tests them in-place - doesn't work!
    public boolean isMinimal_(int[] bondOrderArray, SSPermutationGroup autG) {
        boolean isMin = true;
        for (Permutation p : autG.all()) {
            for (int index = 0; index < bondOrderArray.length; index++) {
                int permutedIndex = p.get(index);
                int permutedValue = bondOrderArray[permutedIndex];
                int value = bondOrderArray[index];
                System.out.println("p(" + index + ") = " + permutedIndex +
                                   ", o[" + index + "] = " + value +
                                   ", o[p[" + index + "]] = " + permutedValue);
                if (permutedValue <= value) {
                    continue;
                } else {
                    System.out.println(index + " : " + permutedIndex + " = " 
                                       + permutedValue + " > " + bondOrderArray[index]
                                       + " in " + Arrays.toString(bondOrderArray)
                                       + " under " + p);
                    isMin = false;
                    break;
                }
            }
        }
        return isMin;
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
        parentSignature = molSig.toCanonicalString();
        
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
}
