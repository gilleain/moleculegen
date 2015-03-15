package generate;

import io.AtomContainerPrinter;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond.Order;

import combinatorics.MultiKSubsetLister;

public class BaseAtomChildLister extends BaseChildLister {
    
    public BaseAtomChildLister() {
        super();
    }
    
    public BaseAtomChildLister(String elementString) {
        super(elementString);
    }
    
    public IAtomContainer makeChild(
            IAtomContainer parent, int[] bondOrderArr, int lastIndex) {
        try {
            IAtomContainer child = (IAtomContainer) parent.clone();
            
            String atomSymbol = getElementSymbols().get(lastIndex);
            IAtom newAtom = child.getBuilder().newInstance(IAtom.class, atomSymbol); 
            int maxBos = super.getMaxBondOrderSum(lastIndex);
            child.addAtom(newAtom);

            int implHCount = maxBos;
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
                    IAtom partner = child.getAtom(index);
                    Integer hCount = partner.getImplicitHydrogenCount();
                    int partnerCount = (hCount == null)? 0 : hCount;
                    partner.setImplicitHydrogenCount(partnerCount - value);
                    implHCount -= value;
                }
            }
            newAtom.setImplicitHydrogenCount(implHCount);
//            System.out.println(java.util.Arrays.toString(bondOrderArr) + "\t" 
//                    + test.AtomContainerPrinter.toString(child));
            return child;
        } catch (CloneNotSupportedException cnse) {
            // TODO
            return null;
        }
    }

    public IAtomContainer makeDisconnectedChild(IAtomContainer parent, int lastIndex) {
        try {
            IAtomContainer child = (IAtomContainer) parent.clone();
            
            String atomSymbol = getElementSymbols().get(lastIndex);
            IAtom newAtom = child.getBuilder().newInstance(IAtom.class, atomSymbol); 
            int maxBos = super.getMaxBondOrderSum(lastIndex);
            child.addAtom(newAtom);
            newAtom.setImplicitHydrogenCount(maxBos);
//            System.out.println(java.util.Arrays.toString(bondOrderArr) + "\t" 
//                    + test.AtomContainerPrinter.toString(child));
            child.setProperty("IS_CONNECTED", false);    // UGH
            return child;
        } catch (CloneNotSupportedException cnse) {
            // TODO
            return null;
        }
    }
    
    public List<int[]> getBondOrderArrays(
            IAtomContainer parent, int currentAtomIndex, int maxDegreeSumForCurrent, int maxDegree) {
        // these are the atom indices that can have bonds added
        List<Integer> baseSet = new ArrayList<Integer>();
        
        // get the amount each atom is under-saturated
        int[] saturationCapacity = getSaturationCapacity(parent);
        for (int index = 0; index < parent.getAtomCount(); index++) {
            if (saturationCapacity[index] > 0) {
                baseSet.add(index);
            }
        }
        
        // the possible extensions
        List<int[]> bondOrderArrays = new ArrayList<int[]>();
        
        // no extension possible
        if (baseSet.size() == 0) {
            return bondOrderArrays;
        }
        
        for (int k = 1; k <= maxDegreeSumForCurrent; k++) {
            MultiKSubsetLister<Integer> lister = new MultiKSubsetLister<Integer>(k, baseSet);
            for (List<Integer> multiset : lister) {
                int[] bondOrderArray = 
                    toIntArray(multiset, parent.getAtomCount(), maxDegree, saturationCapacity);
                if (bondOrderArray != null) {
//                    System.out.println(
//                            "converting to " + java.util.Arrays.toString(bondOrderArray)
//                            + " from " + multiset 
//                            + " for " + AtomContainerPrinter.toString(parent)
//                    );
                    bondOrderArrays.add(bondOrderArray);
                }
            }
        }
        return bondOrderArrays;
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
    
}
