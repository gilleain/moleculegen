package appbranch.augment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;

import combinatorics.KSubsetLister;
import combinatorics.MultiKSubsetLister;

public class SaturationCalculator {
    
    /**
     * TODO : this is a very crude method
     * The max BOS is the maximum sum of bond orders of the bonds 
     * attached to an atom of this element type. eg if maxBOS = 4, 
     * then the atom can have any of {{4}, {3, 1}, {2, 2}, {2, 1, 1}, ...} 
     */
    private Map<String, Integer> maxBondOrderSumMap;
    
    /**
     * TODO : this is a very crude method
     * The max bond order is the maximum order of any bond attached.
     */
    private Map<String, Integer> maxBondOrderMap;
    
    private List<String> elementSymbols;
    
    public SaturationCalculator(List<String> elementSymbols) {
        this.elementSymbols = elementSymbols;
        
        maxBondOrderSumMap = new HashMap<String, Integer>();
        maxBondOrderSumMap.put("C", 4);
        maxBondOrderSumMap.put("O", 2);
        maxBondOrderSumMap.put("N", 3);
        
        maxBondOrderMap = new HashMap<String, Integer>();
        maxBondOrderMap.put("C", 3);
        maxBondOrderMap.put("O", 2);
        maxBondOrderMap.put("N", 3);
    }
    
    public int getMaxBondOrderSum(int index) {
        return maxBondOrderSumMap.get(elementSymbols.get(index));
    }
    
    public int getMaxBondOrder(int currentAtomIndex) {
        return maxBondOrderMap.get(elementSymbols.get(currentAtomIndex));
    }
    
    public List<int[]> getBondOrderArrays(
            List<Integer> baseSet, int atomCount, int maxDegreeSumForCurrent, int maxDegree, int[] saturationCapacity) {
        // the possible extensions
        List<int[]> bondOrderArrays = new ArrayList<int[]>();
        
        // no extension possible
        if (baseSet.size() == 0) {
            return bondOrderArrays;
        } else {
            for (int k = 1; k <= maxDegreeSumForCurrent; k++) {
                MultiKSubsetLister<Integer> lister = new MultiKSubsetLister<Integer>(k, baseSet);
                for (List<Integer> multiset : lister) {
                    int[] bondOrderArray = 
                        toIntArray(multiset, atomCount, maxDegree, saturationCapacity);
                    if (bondOrderArray != null) {
                        bondOrderArrays.add(bondOrderArray);
                    }
                }
            }
            return bondOrderArrays;
        }
    }
    
    public int[] toIntArray(List<Integer> multiset, int size, int maxDegree, int[] satCap) {
        int[] intArray = new int[size];
        for (int atomIndex : multiset) {
            if (atomIndex >= size) return null; // XXX
            intArray[atomIndex]++;
            // XXX avoid quadruple bonds and oversaturation 
            if (intArray[atomIndex] > maxDegree || intArray[atomIndex] > satCap[atomIndex]) {
                return null;   
            }
        }
//        System.out.println(multiset + "\t" + Arrays.toString(intArray) + "\t" + Arrays.toString(satCap));
        return intArray;
    }
    
    public int[] getSaturationCapacity(IAtomContainer parent) {
        int[] satCap = new int[parent.getAtomCount()];
        for (int index = 0; index < parent.getAtomCount(); index++) {
            IAtom atom = parent.getAtom(index);
            int maxDegree = maxBondOrderSumMap.get(atom.getSymbol());
            int degree = 0;
            for (IBond bond : parent.getConnectedBondsList(atom)) {
                degree += bond.getOrder().ordinal() + 1;
            }
            satCap[index] = maxDegree - degree;
        }
        return satCap;
    }
    
    public List<Integer> getUndersaturatedAtoms(int atomCount, int[] saturationCapacity) {
        List<Integer> baseSet = new ArrayList<Integer>();
        
        // get the amount each atom is under-saturated
        for (int index = 0; index < atomCount; index++) {
            if (saturationCapacity[index] > 0) {
                baseSet.add(index);
            }
        }
        return baseSet;
    }

    public List<List<Integer>> getUndersaturatedBonds(IAtomContainer atomContainer, List<Integer> undersaturatedAtoms) {
        KSubsetLister<Integer> lister = new KSubsetLister<Integer>(2, undersaturatedAtoms);
        List<List<Integer>> pairs = new ArrayList<List<Integer>>();
        for (List<Integer> pair : lister) {
            int first = pair.get(0);
            int second = pair.get(1);
            if (second >= atomContainer.getAtomCount()) {
                pairs.add(pair);
            } else {
                IBond bond = atomContainer.getBond(atomContainer.getAtom(first), atomContainer.getAtom(second));
                if (bond == null || bond.getOrder() == Order.SINGLE || bond.getOrder() == Order.DOUBLE) {
                    pairs.add(pair);
                }
            }
        }
        return pairs;
    }

}
