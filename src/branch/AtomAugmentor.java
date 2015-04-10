package branch;

import group.AtomDiscretePartitionRefiner;
import group.PermutationGroup;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class AtomAugmentor implements Augmentor {
    
    /**
     * The elements (in order) used to make molecules for this run.
     */
    private List<String> elementSymbols;
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    public AtomAugmentor(String elementString) {
        elementSymbols = new ArrayList<String>();
        for (int i = 0; i < elementString.length(); i++) {
            elementSymbols.add(String.valueOf(elementString.charAt(i)));
        }
    }
    
    /**
     * @return the initial structure
     */
    public Augmentation getInitial() {
        String elementSymbol = elementSymbols.get(0);
        IAtom initialAtom = builder.newInstance(IAtom.class, elementSymbol);
        return new AtomAugmentation(initialAtom);
    }

    @Override
    public List<Augmentation> augment(Augmentation parent) {
        IAtomContainer atomContainer = parent.getAugmentedMolecule();
        List<Augmentation> augmentations = new ArrayList<Augmentation>();
        String elementSymbol = getNextElementSymbol(parent);
        for (int[] bondOrders : getBondOrderArrays(atomContainer)) {
            IAtom atomToAdd = builder.newInstance(IAtom.class, elementSymbol);
            augmentations.add(new AtomAugmentation(atomContainer, atomToAdd, bondOrders));
        }
        
        return augmentations;
    }
    
    private String getNextElementSymbol(Augmentation parent) {
        return elementSymbols.get(parent.getAugmentedMolecule().getAtomCount());
    }
    
    private List<int[]> getBondOrderArrays(IAtomContainer atomContainer) {
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        PermutationGroup autG = refiner.getAutomorphismGroup(atomContainer);
        return new ArrayList<int[]>();  // XXX
    }

}
