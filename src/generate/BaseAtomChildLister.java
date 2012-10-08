package generate;

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

import combinatorics.MultiKSubsetLister;

public class BaseAtomChildLister {
    
    /**
     * TODO : this is a very crude method
     */
    private Map<String, Integer> degreeMap;
    
    /**
     * The elements (in order) used to make this molecule.
     */
    private List<String> elementSymbols;
    
    public BaseAtomChildLister() {
        degreeMap = new HashMap<String, Integer>();
        degreeMap.put("C", 4);
        degreeMap.put("O", 3);
    }
    
    public int getMaxDegree(int index) {
        return degreeMap.get(elementSymbols.get(index));
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
            System.out.println(Arrays.toString(bondOrderArr) + "\t" 
                    + test.AtomContainerPrinter.toString(child));
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

}
