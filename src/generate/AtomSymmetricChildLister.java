package generate;

import group.Permutation;
import group.SSPermutationGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.signature.Orbit;

/**
 * List candidate children of a parent molecule, by connecting a new atom to the existing atoms 
 * through a set of bonds that are minimal in their orbit.
 * 
 * @author maclean
 *
 */
public class AtomSymmetricChildLister extends BaseAtomChildLister implements SignatureChildLister {
    
    /**
     * An optimisation : since the signature of the parent has to be calculated to
     * make the group, we calculate it here to be used in the validator for the 
     * canonical checking step 
     */
    private String parentSignature;
    
    public AtomSymmetricChildLister() {
        super();
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
    
    public String getParentSignature() {
        return parentSignature;
    }
    
    public List<IAtomContainer> listChildren(IAtomContainer parent, int currentAtomIndex) {
        int maxDegreeSumForCurrent = getMaxBondOrderSum(currentAtomIndex);
        int maxDegreeForCurrent = getMaxBondOrder(currentAtomIndex);
        SSPermutationGroup autG = getGroup(parent);
        List<IAtomContainer> children = new ArrayList<IAtomContainer>();
        int maxMultisetSize = Math.min(currentAtomIndex, maxDegreeSumForCurrent);
//        System.out.println("mms " + maxMultisetSize);
        for (List<Integer> multiset : getMultisets(parent, maxMultisetSize)) {
            int[] bondOrderArray = toIntArray(multiset, maxMultisetSize, maxDegreeForCurrent);
            if (bondOrderArray != null && isMinimal(bondOrderArray, autG)) {
//                System.out.println(Arrays.toString(bondOrderArray));
                children.add(makeChild(parent, bondOrderArray, currentAtomIndex));
            }
        }
        return children;
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

    public SSPermutationGroup getGroup(IAtomContainer parent) {
        MoleculeSignature molSig = new MoleculeSignature(parent);
        parentSignature = molSig.toCanonicalString();
        
        List<Orbit> orbits = molSig.calculateOrbits();
        int size = parent.getAtomCount();
        SSPermutationGroup autG = new SSPermutationGroup(size);
        for (Orbit orbit : orbits) {
           addOrbit(orbit, autG, size);
        }
        return autG;
    }
    
    private void addOrbit(Orbit orbit, SSPermutationGroup autG, int size) {
        List<Integer> atomIndices = orbit.getAtomIndices();
        int aN = atomIndices.size();
        if (aN == 2) {
            Permutation pp = new Permutation(size);
            int i = atomIndices.get(0);
            int j = atomIndices.get(1);
            pp.set(i, j);
            pp.set(j, i);
//            System.out.println("entering " + pp);
            autG.enter(pp);
        } else if (aN >= 3) {
            int first = atomIndices.get(0);
            int second = atomIndices.get(1);
            
            // p1 is (0, 1)
            Permutation pp1 = new Permutation(size);
            pp1.set(first, second);
            pp1.set(second, first);
//            System.out.println("entering " + pp1);
            autG.enter(pp1);
            
            // p2 is (1, 2, ...., n, 0)
            Permutation pp2 = new Permutation(size);
            for (int i = 0; i < aN - 1; i++) {
                pp2.set(atomIndices.get(i), atomIndices.get(i + 1));
            }
            pp2.set(atomIndices.get(aN - 1), atomIndices.get(0));
//            System.out.println("entering " + pp2);
            autG.enter(pp2);
        }
    }
}
