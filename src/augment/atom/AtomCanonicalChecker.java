package augment.atom;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.openscience.cdk.interfaces.IAtomContainer;

import augment.CanonicalChecker;
import group.AtomDiscretePartitionRefiner;
import group.Partition;
import group.Permutation;
import util.molecule.CutCalculator;

public class AtomCanonicalChecker implements CanonicalChecker<AtomAugmentation> {

    public boolean isCanonical(AtomAugmentation atomAugmentation) {
        IAtomContainer augmentedMolecule = atomAugmentation.getAugmentedObject();
        
        if (augmentedMolecule.getAtomCount() <= 2) {
            return true;
        }
        
        List<Integer> nonSeparatingAtoms = getNonSeparatingAtoms(augmentedMolecule);
//        System.out.println(nonSeparatingAtoms);
        if (nonSeparatingAtoms.size() == 0) {
            return true;
        }

        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.getAutomorphismGroup(augmentedMolecule);

        int chosen = getChosen(nonSeparatingAtoms, refiner.getBest());
        int last = augmentedMolecule.getAtomCount() - 1;
        return inOrbit(chosen, last, refiner.getAutomorphismPartition());
    }
    
    private boolean inOrbit(int chosen, int last, Partition orbits) {
//        System.out.println("chosen " + chosen + " last " + last + " " + orbits);
        for (int cellIndex = 0; cellIndex < orbits.size(); cellIndex++) {
            SortedSet<Integer> orbit = orbits.getCell(cellIndex);
            if (orbit.contains(chosen) && orbit.contains(last)) {
                return true;
            }
        }
        return false;   
    }
    
    // TODO : combine this method with get non separating atoms
    private int getChosen(List<Integer> nonSeparatingAtoms, Permutation labelling) {
        for (int index = labelling.size() - 1; index >= 0; index--) {
            int label = labelling.get(index);
            if (nonSeparatingAtoms.contains(label)) {
                return label;
            } else {
                continue;
            }
        }
        return -1;  // XXX shouldn't happen...
    }
    
    private List<Integer> getNonSeparatingAtoms(IAtomContainer mol) {
        List<Integer> nonSeparatingVertices = new ArrayList<Integer>(); 
        List<Integer> cutVertices = CutCalculator.getCutVertices(mol);
//        System.out.println(cutVertices);
        for (int index = 0; index < mol.getAtomCount(); index++) {
            if (cutVertices.contains(index)) {
                continue;
            } else {
                nonSeparatingVertices.add(index);
            }
        }
//        System.out.println(io.AtomContainerPrinter.toString(mol) + " " + nonSeparatingVertices);
        return nonSeparatingVertices;
    }
 
}
