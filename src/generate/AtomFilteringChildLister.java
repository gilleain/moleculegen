package generate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.signature.MoleculeSignature;

import combinatorics.MultiKSubsetLister;

/**
 * List candidate children of a parent molecule, by connecting a new atom to the existing atoms 
 * through a set of bonds that are minimal in their orbit.
 * 
 * @author maclean
 *
 */
public class AtomFilteringChildLister implements SignatureChildLister {
    
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
    
    public AtomFilteringChildLister() {
        degreeMap = new HashMap<String, Integer>();
        degreeMap.put("C", 4);
        degreeMap.put("O", 3);
    }
    
    /**
     * Convenience constructor for testing.
     * 
     * @param elementString
     */
    public AtomFilteringChildLister(String elementString) {
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
        List<IAtomContainer> children = new ArrayList<IAtomContainer>();
        List<String> certs = new ArrayList<String>();
        int maxMultisetSize = Math.min(currentAtomIndex, maxDegreeForCurrent);
        parentSignature = new MoleculeSignature(parent).toCanonicalString();
        for (List<Integer> multiset : getMultisets(parent, maxMultisetSize)) {
            
            int[] bondOrderArray = toIntArray(multiset, maxMultisetSize);
            if (bondOrderArray != null) {
                IAtomContainer child = makeChild(parent, bondOrderArray, currentAtomIndex);
                MoleculeSignature molSig = new MoleculeSignature(child);
                String molSigString = molSig.toCanonicalString();
                if (certs.contains(molSigString)) {
                    continue;
                } else {
                    children.add(child);
                    certs.add(molSigString);
                }
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
