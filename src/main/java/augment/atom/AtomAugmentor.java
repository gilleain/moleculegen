package augment.atom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import augment.Augmentor;
import augment.SaturationCalculator;
import augment.constraints.ElementConstraints;
import group.AtomDiscretePartitionRefiner;
import group.Permutation;
import group.PermutationGroup;

public class AtomAugmentor implements Augmentor<AtomAugmentation> {
    
    private static final long serialVersionUID = -1795216862782671835L;

    public static IChemObjectBuilder getBuilder() {
        return SilentChemObjectBuilder.getInstance();
    }
    
    /**
     * The elements (in order) used to make molecules for this run.
     */
    private List<String> elementSymbols;
    
    private SaturationCalculator saturationCalculator;
    
    public AtomAugmentor(String elementString) {
        elementSymbols = new ArrayList<String>();
        for (int i = 0; i < elementString.length(); i++) {
            elementSymbols.add(String.valueOf(elementString.charAt(i)));
        }
        this.saturationCalculator = new SaturationCalculator(elementSymbols);
    }
    
    public AtomAugmentor(List<String> elementSymbols) {
        this.elementSymbols = elementSymbols;
        this.saturationCalculator = new SaturationCalculator(elementSymbols);
    }
    
    @Override
    public List<AtomAugmentation> augment(AtomAugmentation parent) {
        IAtomContainer atomContainer = parent.getAugmentedObject();
        List<AtomAugmentation> augmentations = new ArrayList<AtomAugmentation>();
        ElementConstraints constraints = parent.getConstraints();
        if (constraints == null)
            throw new UnsupportedOperationException("Constraints are null - should not be");
        IChemObjectBuilder builder = getBuilder();
        for (String elementSymbol : constraints) {
            for (int[] bondOrders : getBondOrderArrays(atomContainer, elementSymbol)) {
                IAtom atomToAdd = builder.newInstance(IAtom.class, elementSymbol);
                augmentations.add(
                        new AtomAugmentation(atomContainer, atomToAdd, bondOrders, constraints.minus(elementSymbol)));
            }
        }
        
        return augmentations;
    }
    
    private List<int[]> getBondOrderArrays(IAtomContainer atomContainer, String symbol) {
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        PermutationGroup autG = refiner.getAutomorphismGroup(atomContainer);
        int atomCount = atomContainer.getAtomCount();
        
        // these are the atom indices that can have bonds added
        int[] saturationCapacity = saturationCalculator.getSaturationCapacity(atomContainer);
        List<Integer> baseSet = saturationCalculator.getUndersaturatedAtoms(atomCount, saturationCapacity);
        
        int maxDegreeSumForCurrent = saturationCalculator.getMaxBondOrderSum(symbol);
        int maxDegreeForCurrent = saturationCalculator.getMaxBondOrder(symbol);
        
        List<int[]> representatives = new ArrayList<int[]>();
        for (int[] bondOrderArray : saturationCalculator.getBondOrderArrays(
                baseSet, atomCount, maxDegreeSumForCurrent, maxDegreeForCurrent, saturationCapacity)) {
            if (isMinimal(bondOrderArray, autG)) {
                representatives.add(bondOrderArray);
            }
        }
        // for disconnected graphs
//        int[] emptySet = new int[atomCount];
//        representatives.add(emptySet);
        return representatives;
    }
    
    private boolean isMinimal(int[] bondOrderArray, PermutationGroup autG) {
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
    
}
