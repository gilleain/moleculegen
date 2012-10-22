package validate;

import group.AtomDiscretePartitionRefiner;
import group.Partition;
import group.Permutation;

import java.util.SortedSet;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class BondCanonicalValidator {

    public boolean isCanonical(IAtomContainer atomContainer) {
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.getAutomorphismGroup(atomContainer);
        Permutation labelling = refiner.getBest().invert();
        Partition autPartition = refiner.getAutomorphismPartition();
//        int lastAtomIndex = atomContainer.getAtomCount() - 1;
        int lastBondIndex = atomContainer.getBondCount() - 1;
        IBond bond = atomContainer.getBond(lastBondIndex);
        IBond canDelBond = getCanonicalDeletionBond(atomContainer, labelling);
        int atomIndex0 = atomContainer.getAtomNumber(bond.getAtom(0));
        int atomIndex1 = atomContainer.getAtomNumber(bond.getAtom(1));
//        int del0 = labelling.get(atomIndex0);
//        int del1 = labelling.get(atomIndex1);
        int del0 = atomContainer.getAtomNumber(canDelBond.getAtom(0));
        int del1 = atomContainer.getAtomNumber(canDelBond.getAtom(1));

//        System.out.println(
//                test.AtomContainerPrinter.toString(atomContainer) + "\t" 
//                + atomIndex0 + "\t" + atomIndex1 + "\t" + del0 + "\t" + del1 + "\t"
//                + labelling + "\t" + autPartition
//        );
        if (del0 == atomIndex0 && del1 == atomIndex1) {
            return true;
        } else {
            return inSameCell(autPartition, del0, atomIndex0)
                && inSameCell(autPartition, del1, atomIndex1);
        }
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
    
    private boolean inSameCell(Partition partition, int i, int j) {
        for (int cellIndex = 0; cellIndex < partition.size(); cellIndex++) {
            SortedSet<Integer> cell = partition.getCell(cellIndex);
            if (cell.contains(i) && cell.contains(j)) {
                return true;
            }
        }
        return false;
    }

}
