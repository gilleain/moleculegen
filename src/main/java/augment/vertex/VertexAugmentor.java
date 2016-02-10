package augment.vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import augment.Augmentor;
import augment.constraints.VertexColorConstraints;
import group.Permutation;
import group.PermutationGroup;
import group.graph.GraphDiscretePartitionRefiner;
import model.Graph;
import util.graph.SaturationCalculator;

/**
 * An augmentor that uses vertex augmentations.
 * 
 * @author maclean
 *
 */
public class VertexAugmentor implements Augmentor<ByVertexAugmentation> {
    
    /**
     * The elements (in order) used to make molecules for this run.
     */
    private List<String> elementSymbols;
    
    private SaturationCalculator saturationCalculator;
    
    public VertexAugmentor(String elementString) {
        elementSymbols = new ArrayList<String>();
        for (int i = 0; i < elementString.length(); i++) {
            elementSymbols.add(String.valueOf(elementString.charAt(i)));
        }
        this.saturationCalculator = new SaturationCalculator(elementSymbols);
    }
    
    public VertexAugmentor(List<String> elementSymbols) {
        this.elementSymbols = elementSymbols;
        this.saturationCalculator = new SaturationCalculator(elementSymbols);
    }
    
    @Override
    public List<ByVertexAugmentation> augment(ByVertexAugmentation parent) {
        Graph atomContainer = parent.getAugmentedObject();
        List<ByVertexAugmentation> augmentations = new ArrayList<ByVertexAugmentation>();
        VertexColorConstraints constraints = parent.getConstraints();
        for (String elementSymbol : constraints) {
            for (int[] edgeColors : getEdgeColorArrays(atomContainer, elementSymbol)) {
                VertexColorConstraints newConstraints = constraints.minus(elementSymbol);
                augmentations.add(
                        new ByVertexAugmentation(
                                atomContainer, elementSymbol, edgeColors, newConstraints));
            }
        }
        
        return augmentations;
    }
    
    private List<int[]> getEdgeColorArrays(Graph atomContainer, String symbol) {
        GraphDiscretePartitionRefiner refiner = new GraphDiscretePartitionRefiner();
        PermutationGroup autG = refiner.getAutomorphismGroup(atomContainer);
        int atomCount = atomContainer.getVertexCount();
        
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
