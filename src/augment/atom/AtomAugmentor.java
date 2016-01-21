package augment.atom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import augment.Augmentation;
import augment.Augmentor;
import augment.ExtensionSource;
import augment.SaturationCalculator;
import group.AtomDiscretePartitionRefiner;
import group.Permutation;
import group.PermutationGroup;

public class AtomAugmentor implements Augmentor<IAtomContainer, AtomExtension> {
    
    /**
     * The elements (in order) used to make molecules for this run.
     */
    private List<String> elementSymbols;
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    private SaturationCalculator saturationCalculator;
    
    private ExtensionSource<Integer, String> extensionSource;
    
    public AtomAugmentor(String elementString, ExtensionSource<Integer, String> extensionSource) {
        elementSymbols = new ArrayList<String>();
        for (int i = 0; i < elementString.length(); i++) {
            elementSymbols.add(String.valueOf(elementString.charAt(i)));
        }
        this.saturationCalculator = new SaturationCalculator(elementSymbols);
        this.extensionSource = extensionSource;
    }
    
    public AtomAugmentor(List<String> elementSymbols, ExtensionSource<Integer, String> extensionSource) {
        this.elementSymbols = elementSymbols;
        this.saturationCalculator = new SaturationCalculator(elementSymbols);
        this.extensionSource = extensionSource;
    }
    
    @Override
    public List<Augmentation<IAtomContainer, AtomExtension>> augment(Augmentation<IAtomContainer, AtomExtension> parent) {
        IAtomContainer atomContainer = parent.getBase();
        List<Augmentation<IAtomContainer, AtomExtension>> augmentations = 
                new ArrayList<Augmentation<IAtomContainer, AtomExtension>>();
        String elementSymbol = extensionSource.getNext(atomContainer.getAtomCount());
        for (int[] bondOrders : getBondOrderArrays(atomContainer)) {
            IAtom atomToAdd = builder.newInstance(IAtom.class, elementSymbol);
            augmentations.add(new AtomAugmentation(atomContainer, atomToAdd, bondOrders));
        }
        
        return augmentations;
    }
    
    private List<int[]> getBondOrderArrays(IAtomContainer atomContainer) {
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        PermutationGroup autG = refiner.getAutomorphismGroup(atomContainer);
        int atomCount = atomContainer.getAtomCount();
        
        // these are the atom indices that can have bonds added
        int[] saturationCapacity = saturationCalculator.getSaturationCapacity(atomContainer);
        List<Integer> baseSet = saturationCalculator.getUndersaturatedAtoms(atomCount, saturationCapacity);
        
        int maxDegreeSumForCurrent = saturationCalculator.getMaxBondOrderSum(atomCount);
        int maxDegreeForCurrent = saturationCalculator.getMaxBondOrder(atomCount);
        
        List<int[]> representatives = new ArrayList<int[]>();
        for (int[] bondOrderArray : saturationCalculator.getBondOrderArrays(
                baseSet, atomCount, maxDegreeSumForCurrent, maxDegreeForCurrent, saturationCapacity)) {
            if (isMinimal(bondOrderArray, autG)) {
                representatives.add(bondOrderArray);
            }
        }
        int[] emptySet = new int[atomCount];
        representatives.add(emptySet);
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
