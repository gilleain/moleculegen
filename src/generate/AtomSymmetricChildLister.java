package generate;

import group.AtomDiscretePartitionRefiner;
import group.Permutation;
import group.PermutationGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * List candidate children of a parent molecule, by connecting a new atom to the existing atoms 
 * through a set of bonds that are minimal in their orbit.
 * 
 * @author maclean
 *
 */
public class AtomSymmetricChildLister extends BaseAtomChildLister implements ChildLister {
    
    public AtomSymmetricChildLister() {
        super();
    }
    
    /**
     * Convenience constructor for testing.
     * 
     * @param elementString
     */
    public AtomSymmetricChildLister(List<String> elementSymbols) {
        this();
        setElementSymbols(elementSymbols);
    }
    
    public List<IAtomContainer> listChildren(IAtomContainer parent, int currentAtomIndex) {
        int maxDegreeSumForCurrent = getMaxBondOrderSum(currentAtomIndex);
        int maxDegreeForCurrent = getMaxBondOrder(currentAtomIndex);
        PermutationGroup autG = getGroup(parent);
        List<IAtomContainer> children = new ArrayList<IAtomContainer>();
        for (int[] bondOrderArray : getBondOrderArrays(
                parent, currentAtomIndex, maxDegreeSumForCurrent, maxDegreeForCurrent)) {
            if (isMinimal(bondOrderArray, autG)) {
//                System.out.println(Arrays.toString(bondOrderArray));
                children.add(makeChild(parent, bondOrderArray, currentAtomIndex));
            }
        }
        
        children.add(makeDisconnectedChild(parent, currentAtomIndex));
        
        return children;
    }

    public boolean isMinimal(int[] bondOrderArray, PermutationGroup autG) {
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
    public boolean isMinimal_(int[] bondOrderArray, PermutationGroup autG) {
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

    public PermutationGroup getGroup(IAtomContainer parent) {
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        return refiner.getAutomorphismGroup(parent);
    }
}
