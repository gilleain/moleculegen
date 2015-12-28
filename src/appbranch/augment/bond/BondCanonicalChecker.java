package appbranch.augment.bond;

import org.openscience.cdk.interfaces.IAtomContainer;

import appbranch.augment.Augmentation;
import appbranch.canonical.CanonicalChecker;
import group.BondDiscretePartitionRefiner;
import group.Permutation;
import group.PermutationGroup;

/**
 * Check a bond-wise augmentation of an IAtomContainer for canonicity. 
 * 
 * @author maclean
 *
 */
public class BondCanonicalChecker implements CanonicalChecker<IAtomContainer, BondExtension> {

    @Override
    public boolean isCanonical(Augmentation<IAtomContainer, BondExtension> augmentation) {
//        return true;
        IAtomContainer augmentedMolecule = augmentation.getBase();
        if (augmentedMolecule.getAtomCount() <= 2 || augmentedMolecule.getBondCount() == 0) {
            return true;
        }
        BondDiscretePartitionRefiner bondRefiner = new BondDiscretePartitionRefiner();
        PermutationGroup autBondH = bondRefiner.getAutomorphismGroup(augmentedMolecule);
        int addedBondIndex = getAddedBondIndex(augmentedMolecule, augmentation.getExtension());
        int canDelIndex = getCanonicalDeletionBondIndex(bondRefiner);
        return inOrbit(addedBondIndex, canDelIndex, autBondH);
    }
    
    private boolean inOrbit(int addedBondIndex, int canDelIndex, PermutationGroup autBondH) {
        for (Permutation permutation : autBondH.all()) {
            if (permutation.get(addedBondIndex) == canDelIndex) {
                return true;
            }
        }
        return false;
    }

    private int getCanonicalDeletionBondIndex(BondDiscretePartitionRefiner bondRefiner) {
        Permutation labelling = bondRefiner.getBest();
        // TODO : bridge edges
        return labelling.get(labelling.size() - 1);
    }

    private int getAddedBondIndex(IAtomContainer augmentedMolecule, BondExtension extension) {
        return augmentedMolecule.getBondNumber(
                augmentedMolecule.getAtom(extension.getIndexPair().getStart()), 
                augmentedMolecule.getAtom(extension.getIndexPair().getEnd()));
    }

}
