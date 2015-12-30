package appbranch.augment.bond;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import appbranch.augment.Augmentation;
import appbranch.canonical.CanonicalChecker;
import group.AtomDiscretePartitionRefiner;
import group.BondDiscretePartitionRefiner;
import group.Permutation;
import group.PermutationGroup;
import validate.BondCanonicalValidator;

/**
 * Check a bond-wise augmentation of an IAtomContainer for canonicity. 
 * 
 * @author maclean
 *
 */
public class BondCanonicalChecker implements CanonicalChecker<IAtomContainer, BondExtension> {

    @Override
    public boolean isCanonical(Augmentation<IAtomContainer, BondExtension> augmentation) {
        IAtomContainer augmentedMolecule = augmentation.getBase();
        if (augmentedMolecule.getAtomCount() <= 2 || augmentedMolecule.getBondCount() == 0) {
            return true;
        }
        if (!inOrder(augmentedMolecule)) {
            return false;
        }
        
//        BondDiscretePartitionRefiner bondRefiner = new BondDiscretePartitionRefiner();
//        PermutationGroup autBondH = bondRefiner.getAutomorphismGroup(augmentedMolecule);
//        System.out.println(io.AtomContainerPrinter.toString(augmentedMolecule) + " / " + bondRefiner.getFirst());
//        System.out.println(bondRefiner.getAutomorphismPartition());
        IBond addedBond = getAddedBondIndex(augmentedMolecule, augmentation.getExtension());
//        int canDelIndex = getCanonicalDeletionBondIndex(bondRefiner);
        
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        PermutationGroup aut = refiner.getAutomorphismGroup(augmentedMolecule);
        Permutation labelling = refiner.getBest();
        IBond canDelBond = getCanonicalDeletionBond(augmentedMolecule, labelling);
//        int canDelIndex = augmentedMolecule.getBondNumber(canDelBond);
        int ai = augmentedMolecule.getAtomNumber(addedBond.getAtom(0));
        int aj = augmentedMolecule.getAtomNumber(addedBond.getAtom(1));
        int bi = augmentedMolecule.getAtomNumber(canDelBond.getAtom(0));
        int bj = augmentedMolecule.getAtomNumber(canDelBond.getAtom(1));
        return inOrbit(ai, aj, bi, bj, aut);
    }
    
    private boolean inOrder(IAtomContainer augmentedMolecule) {
        String prev = null;
        for (IBond bond : augmentedMolecule.bonds()) {
            String current = toString(augmentedMolecule, bond);
            if (prev == null || prev.compareTo(current) < 0) {
                prev = current;
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
    
    private String toString(IAtomContainer atomContainer, IBond bond) {
        return atomContainer.getAtomNumber(bond.getAtom(0)) + ":" 
              + atomContainer.getAtomNumber(bond.getAtom(1)); 
    }
    
    private boolean inOrbit(int addedBondI, int addedBondJ, int canDelBondI, int canDelBondJ, PermutationGroup aut) {
        for (Permutation p : aut.all()) {
            int pi = p.get(addedBondI);
            int pj = p.get(addedBondJ);
            if ((pi == canDelBondI && pj == canDelBondJ) || (pj == canDelBondI && pi == canDelBondJ)) {
                return true;
            }
        }
        return false;
    }
    
    private IBond getCanonicalDeletionBond(IAtomContainer atomContainer, Permutation labelling) {
        String largest = null;
        IBond largestBond = null;
        for (IBond bond : atomContainer.bonds()) {
            int atomIndex0 = labelling.get(atomContainer.getAtomNumber(bond.getAtom(0)));
            int atomIndex1 = labelling.get(atomContainer.getAtomNumber(bond.getAtom(1)));
            String bondAsStr;
            if (atomIndex0 < atomIndex1) {
                bondAsStr = atomIndex0 + ":" + atomIndex1;
            } else {
                bondAsStr = atomIndex1 + ":" + atomIndex0;
            }
            if (largest == null || largest.compareTo(bondAsStr) < 0) {
                largest = bondAsStr;
                largestBond = bond;
            }
        }
        return largestBond;
    }
    
    private boolean inOrbit(int addedBondIndex, int canDelIndex, PermutationGroup autBondH) {
        for (Permutation permutation : autBondH.all()) {
            if (permutation.get(addedBondIndex) == canDelIndex) {
                return true;
            }
        }
        return false;
    }
    
    private Permutation getBondLabelling(IAtomContainer augmentedMolecule) {
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        Permutation atomLabelling = refiner.getBest();
        int[] bondLabelling = new int[augmentedMolecule.getBondCount()];
        for (IBond bond : augmentedMolecule.bonds()) {
            int a0 = augmentedMolecule.getAtomNumber(bond.getAtom(0));
            int a1 = augmentedMolecule.getAtomNumber(bond.getAtom(1));
            int pA0 = atomLabelling.get(a0);
            int pA1 = atomLabelling.get(a1);
            
        }
        return new Permutation(0);  /// XXX todo
    }

    private int getCanonicalDeletionBondIndex(BondDiscretePartitionRefiner bondRefiner) {
        Permutation labelling = bondRefiner.getBest();
        // TODO : bridge edges
        return labelling.get(labelling.size() - 1);
    }

    private IBond getAddedBondIndex(IAtomContainer augmentedMolecule, BondExtension extension) {
        return augmentedMolecule.getBond(
                augmentedMolecule.getAtom(extension.getIndexPair().getStart()), 
                augmentedMolecule.getAtom(extension.getIndexPair().getEnd()));
    }

}
