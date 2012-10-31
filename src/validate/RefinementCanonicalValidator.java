package validate;

import group.AtomDiscretePartitionRefiner;
import group.Partition;
import group.Permutation;

import java.util.SortedSet;

import org.openscience.cdk.interfaces.IAtomContainer;

public class RefinementCanonicalValidator implements CanonicalValidator {
    
    private AtomDiscretePartitionRefiner refiner;
    
    private AtomDiscretePartitionRefiner disconnectedRefiner;
    
    public RefinementCanonicalValidator() {
        refiner = new AtomDiscretePartitionRefiner();
        disconnectedRefiner = new AtomDiscretePartitionRefiner(true);
    }

    @Override
    public boolean isCanonical(IAtomContainer atomContainer) {
        Object connectedProperty = atomContainer.getProperty("IS_CONNECTED");
        boolean isConnected;
        if (connectedProperty == null) {
            isConnected = true; // assume connected
        } else {
            isConnected = (Boolean) connectedProperty;
        }
        
        if (isConnected) {
            return isCanonicalConnected(atomContainer);
        } else {
            return isCanonicalDisconnected(atomContainer);
        }
    }
    
    private boolean isCanonicalConnected(IAtomContainer atomContainer) {
        refiner.reset();
//        System.out.println("Conn : " + test.AtomContainerPrinter.toString(atomContainer));
        refiner.getAutomorphismGroup(atomContainer);
        Permutation labelling = refiner.getBest().invert();
        Partition partition = refiner.getAutomorphismPartition();
        int size = refiner.getVertexCount() - 1;
        int del = labelling.get(size);
        boolean canonical = (del == size || inSameCell(partition, del, size));
//        boolean canonical = labelling.isIdentity();
        if (canonical) {
//            System.out.println(labelling + "\t" + size + "\t" + del + "\t" + partition);
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isCanonicalDisconnected(IAtomContainer atomContainer) {
        if (atomContainer.getBondCount() == 0) {
            return true;
        } else {
            disconnectedRefiner.reset();
//            System.out.println("Disc : " + test.AtomContainerPrinter.toString(atomContainer));
            disconnectedRefiner.getAutomorphismGroup(atomContainer);
            Permutation labelling = disconnectedRefiner.getBest().invert();
            Partition partition = disconnectedRefiner.getAutomorphismPartition();
            int size = disconnectedRefiner.getVertexCount() - 1;
            int del = labelling.get(size);
            return del == size || inSameCell(partition, del, size);
        }
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
