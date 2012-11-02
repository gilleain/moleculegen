package validate;

import group.AtomDiscretePartitionRefiner;
import group.Partition;
import group.Permutation;

import java.util.SortedSet;
import java.util.TreeSet;

import org.openscience.cdk.interfaces.IAtom;
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
        int size = refiner.getVertexCount() - 1;
        
        Permutation labelling = refiner.getBest();
        Permutation inverse = labelling.invert();
        Partition partition = refiner.getAutomorphismPartition();
        partition = translate(partition, inverse);
        int del = inverse.get(size);
        
        boolean canonical = (del == size || inSameCell(partition, del, size));
//        boolean canonical = inverse.isIdentity();
        String acp = test.AtomContainerPrinter.toString(atomContainer);
        if (canonical) {
//            System.out.println("C " + labelling + "\t" + size + "\t" + del + "\t" + partition + "\t" + acp);
            return true;
        } else {
//            System.out.println("N " + labelling + "\t" + size + "\t" + del + "\t" + partition + "\t" + acp);
            return false;
        }
    }
    
    private Partition translate(Partition original, Permutation labelling) {
        Partition translation = new Partition();
        for (int cellIndex = 0; cellIndex < original.size(); cellIndex++) {
            SortedSet<Integer> oCell = original.getCell(cellIndex);
            SortedSet<Integer> tCell = new TreeSet<Integer>();
            for (int i : oCell) {
                tCell.add(labelling.get(i));
            }
            translation.addCell(tCell);
        }
        return translation;
    }
    
    private boolean isCanonicalDisconnected(IAtomContainer atomContainer) {
        IAtom lastAtom = atomContainer.getAtom(atomContainer.getAtomCount() - 1);
        if (atomContainer.getBondCount() == 0 || atomContainer.getConnectedAtomsCount(lastAtom) == 0) {
//            System.out.println("Disc(C): " + test.AtomContainerPrinter.toString(atomContainer));
            return true;
        } else {
            disconnectedRefiner.reset();
            disconnectedRefiner.getAutomorphismGroup(atomContainer);
            int size = disconnectedRefiner.getVertexCount() - 1;
            Partition partition = disconnectedRefiner.getAutomorphismPartition();
            int[] indexMap = disconnectedRefiner.getIndexMap();
            
            Permutation labelling = disconnectedRefiner.getBest();
//            System.out.println(labelling + "\t" + java.util.Arrays.toString(indexMap));
            Permutation inverse = invert(labelling, indexMap);
            addDisconnectedCell(partition, labelling.size(), indexMap);
//            partition = translate(partition, inverse);
            int del = inverse.get(size);
            
            boolean canon = del == size || inSameCell(partition, del, size);
            String acp = test.AtomContainerPrinter.toString(atomContainer);
            if (canon) {
//                System.out.println("C " + inverse + "\t" + size + "\t" + del + "\t" + partition + "\t" + acp);
                return true;
            } else {
//                System.out.println("D " + inverse + "\t" + size + "\t" + del + "\t" + partition + "\t" + acp);
                return false;
            }
        }
    }
    
    private Permutation invert(Permutation p, int[] indexMap) {
        int n = indexMap.length;
        int m = p.size();
        int l = m;
        Permutation inversion = new Permutation(n);
        for (int i = 0; i < n; i++) {
            int j = indexMap[i];
            if (j >= 0) {
                inversion.set(p.get(j), j);
            } else {
                inversion.set(l, l);
                l++;
            }
        }
        return inversion;
    }
    
    private void addDisconnectedCell(Partition partition, int l, int[] indexMap) {
        SortedSet<Integer> cell = new TreeSet<Integer>();
        int j = l;
        for (int i = 0; i < indexMap.length; i++) {
            if (indexMap[i] == -1) {
                cell.add(j);
                j++;
            }
        }
        if (cell.size() > 0) {
            partition.addCell(cell);
        }
//        System.out.println(partition);
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
