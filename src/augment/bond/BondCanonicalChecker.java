package augment.bond;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import group.AtomDiscretePartitionRefiner;
import group.Permutation;
import group.PermutationGroup;
import util.CutCalculator;

/**
 * Check a bond-wise augmentation of an IAtomContainer for canonicity. 
 * 
 * @author maclean
 *
 */
public class BondCanonicalChecker {

    public boolean isCanonical(IAtomContainer augmentedMolecule, BondExtension augmentation) {
        if (augmentedMolecule.getAtomCount() <= 2 || augmentedMolecule.getBondCount() == 0) {
            return true;
        }
        
//        if (!inOrder(augmentedMolecule)) {
//            return false;
//        }
        
        IBond addedBond = getAddedBondIndex(augmentedMolecule, augmentation);
        if (addedBond == null) {
//            System.out.println("disconnected is canonical");
            return true;    // disconnected atoms
        }
        
        List<Integer> nonSeparatingEdges = getNonSeparatingEdges(augmentedMolecule);
        if (nonSeparatingEdges.size() == 0) {
            return true;    // oddly
        }
        
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        PermutationGroup aut = refiner.getAutomorphismGroup(augmentedMolecule);
        Permutation labelling = refiner.getBest();
//        if (!labelling.isIdentity()) return false;
//        System.out.println("best = " + labelling.invert());
        IBond canDelBond = getCanonicalDeletionBond(
                augmentedMolecule, nonSeparatingEdges, labelling.invert());
        
        // note that (ai, aj) are just (extension.start, extension.end)
//        int ai = augmentedMolecule.getAtomNumber(addedBond.getAtom(0));
//        int aj = augmentedMolecule.getAtomNumber(addedBond.getAtom(1));
        int ai = augmentation.getIndexPair().getStart();
        int aj = augmentation.getIndexPair().getEnd();

        int bi = augmentedMolecule.getAtomNumber(canDelBond.getAtom(0));
        int bj = augmentedMolecule.getAtomNumber(canDelBond.getAtom(1));
//        System.out.println("Is: (" + ai + "," + aj + ") should be: (" + bi + "," + bj + ")");
        return inOrbit(ai, aj, bi, bj, aut);
    }
    
    private List<Integer> getNonSeparatingEdges(IAtomContainer mol) {
        List<Integer> cutEdges = CutCalculator.getCutEdges(mol);
        // TODO use set diff
        List<Integer> nonSeparatingEdges = new ArrayList<Integer>();
        for (int index = 0; index < mol.getBondCount(); index++) {
            if (cutEdges.contains(index)) {
                continue;
            } else {
                nonSeparatingEdges.add(index);
            }
        }
        return nonSeparatingEdges;
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
    
    private IBond getCanonicalDeletionBond(
            IAtomContainer atomContainer, List<Integer> nonSeparatingBonds, Permutation labelling) {
        if (nonSeparatingBonds.size() == 1) {
            return atomContainer.getBond(nonSeparatingBonds.get(0));
        }
        
        String largest = null;
        IBond largestBond = null;
        for (int index : nonSeparatingBonds) {
            IBond bond = atomContainer.getBond(index);
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
   
    private IBond getAddedBondIndex(IAtomContainer augmentedMolecule, BondExtension extension) {
        return augmentedMolecule.getBond(
                augmentedMolecule.getAtom(extension.getIndexPair().getStart()), 
                augmentedMolecule.getAtom(extension.getIndexPair().getEnd()));
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

}
