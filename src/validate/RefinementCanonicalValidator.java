package validate;

import group.AtomDiscretePartitionRefiner;
import group.Partition;
import group.Permutation;

import java.util.SortedSet;

import org.openscience.cdk.interfaces.IAtomContainer;

public class RefinementCanonicalValidator implements CanonicalValidator {

    @Override
    public boolean isCanonical(IAtomContainer atomContainer) {
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.getAutomorphismGroup(atomContainer);
        Permutation labelling = refiner.getBest();
        Partition partition = refiner.getAutomorphismPartition();
        int size = atomContainer.getAtomCount() - 1;
        int del = labelling.get(size);
        return del == size || inSameCell(partition, del, size);
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
