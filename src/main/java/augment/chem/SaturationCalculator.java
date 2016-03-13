package augment.chem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import augment.bond.IndexPair;
import combinatorics.KSubsetLister;
import combinatorics.MultiKSubsetLister;

public class SaturationCalculator implements Serializable {
    
    private static final long serialVersionUID = 4524718660608857274L;

    private final BondOrderMaps bondOrderMaps;
    
    public SaturationCalculator() {
        this.bondOrderMaps = new BondOrderMaps();
    }

    public int getMaxBondOrder(String elementSymbol) {
        return bondOrderMaps.getMaxBondOrder(elementSymbol);
    }

    public int getMaxBondOrderSum(String symbol) {
        return bondOrderMaps.getMaxBondOrderSum(symbol);
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
            int maxDegree = bondOrderMaps.getMaxBondOrderSum(atom.getSymbol());
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

    public List<IndexPair> getUndersaturatedBonds(IAtomContainer atomContainer, List<Integer> undersaturatedAtoms, int[] saturationCapacity) {
        List<IndexPair> pairs = new ArrayList<IndexPair>();
        if (undersaturatedAtoms.isEmpty() || undersaturatedAtoms.size() == 1) return pairs;
        for (List<Integer> pair : new KSubsetLister<Integer>(2, undersaturatedAtoms)) {
            int start = pair.get(0);
            int end = pair.get(1);
            IBond bond = atomContainer.getBond(atomContainer.getAtom(start), atomContainer.getAtom(end));
            if (bond == null) {
                int startCapacity = saturationCapacity[start];
                int endCapacity = saturationCapacity[end];
                // run through the possible bond orders from the maximum down to 1
                int maxOrder = Math.min(3, Math.min(startCapacity, endCapacity));
                for (int order = maxOrder; order > 0; order--) {
                   pairs.add(new IndexPair(start, end, order)); 
                }
            }
        }
        return pairs;
    }
   
}
