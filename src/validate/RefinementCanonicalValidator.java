package validate;

import group.AtomDiscretePartitionRefiner;
import group.Partition;
import group.Permutation;

import java.util.SortedSet;
import java.util.TreeSet;

import org.openscience.cdk.interfaces.IAtomContainer;

public class RefinementCanonicalValidator implements CanonicalValidator {
    
    private AtomDiscretePartitionRefiner refiner;
    
//    private AtomDiscretePartitionRefiner disconnectedRefiner;
    
    public RefinementCanonicalValidator() {
        refiner = new AtomDiscretePartitionRefiner();
//        disconnectedRefiner = new AtomDiscretePartitionRefiner(true);
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
            return isCanonicalConnected(atomContainer);
//            return isCanonicalDisconnected(atomContainer);
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
//        String acp = test.AtomContainerPrinter.toString(atomContainer);
        if (canonical) {
//            System.out.println("CC " + inverse + "\t" + size + "\t" + del + "\t" + partition + "\t" + acp);
            return true;
        } else {
//            System.out.println("CN " + inverse + "\t" + size + "\t" + del + "\t" + partition + "\t" + acp);
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
    
//    private boolean isCanonicalDisconnected(IAtomContainer atomContainer) {
////        IAtom lastAtom = atomContainer.getAtom(atomContainer.getAtomCount() - 1);
//        if (atomContainer.getBondCount() == 0 
////                || atomContainer.getConnectedAtomsCount(lastAtom) == 0
//                ) {
////            System.out.println("Disc(C): " + test.AtomContainerPrinter.toString(atomContainer));
//            return true;
//        } else {
//            disconnectedRefiner.reset();
//            disconnectedRefiner.getAutomorphismGroup(atomContainer);
//            
//            // XXX - calculating this twice!
//            Partition elPartition = refiner.getElementPartition(atomContainer);
//            int size = disconnectedRefiner.getVertexCount() - 1;
//            
//            Partition autPartition = disconnectedRefiner.getAutomorphismPartition();
//            int[] indexMap = disconnectedRefiner.getIndexMap();
//            Permutation labelling = disconnectedRefiner.getBest();
//            
//            Map<Integer, List<Integer>> elToAutMap = 
//                    getElToAutPartitionMap(elPartition, autPartition, indexMap);
//            Permutation inverse = fix(
//                    labelling, elPartition, autPartition, elToAutMap, indexMap);
//            Partition translated = translate(autPartition, inverse, indexMap);
//            
//            int del = inverse.get(size);
//            boolean canon = del == size || inSameCell(translated, del, size);
//            
//            String acp = test.AtomContainerPrinter.toString(atomContainer);
//            if (canon) {
////                System.out.println("DC " + labelling + "\t" + inverse + "\t" + size + "\t" + del + "\t" + autPartition + "\t" + translated + "\t" + acp);
//                return true;
//            } else {
////                System.out.println("DN " + labelling + "\t" + inverse + "\t" + size + "\t" + del + "\t" + autPartition + "\t" + translated + "\t" + acp);
//                return false;
//            }
//        }
//    }
    
//    private Map<Integer, List<Integer>> getElToAutPartitionMap(
//            Partition elPart, Partition autPart, int[] indexMap) {
//        Map<Integer, List<Integer>> elToAutMap = new HashMap<Integer, List<Integer>>();
//        for (int elCellIndex = 0; elCellIndex < elPart.size(); elCellIndex++) {
//            SortedSet<Integer> elCell = elPart.getCell(elCellIndex);
//            List<Integer> autCellIndices = new ArrayList<Integer>();
//            for (int element : elCell) {
//                int mapped = indexMap[element];
//                int autCellIndex = cellIndex(mapped, autPart);
//                // will be -1 if disconnected
//                if (autCellIndex != -1 && !autCellIndices.contains(autCellIndex)) {
//                    autCellIndices.add(autCellIndex);
//                }
//            }
//            elToAutMap.put(elCellIndex, autCellIndices);
//        }
//        return elToAutMap;
//    }
//    
//    private Permutation fix(
//            Permutation labelling, 
//            Partition elPart, Partition autPart,
//            Map<Integer, List<Integer>> elToAutMap, int[] indexMap) {
//        
//        // each element cell has 0 or more disconnected atoms, 
//        // so we use this array to keep track of the indices
//        int[] discCounters = new int[elPart.size()];
//        for (int elCellIndex : elToAutMap.keySet()) {
//            List<Integer> autCellIndices = elToAutMap.get(elCellIndex);
//            if (autCellIndices.size() == 0) {
//                // disconnected cell
//            } else {
//                int connectedCount = 0;
//                for (int autCellIndex : autCellIndices) {
//                    SortedSet<Integer> currentCell = autPart.getCell(autCellIndex);
//                    connectedCount += currentCell.size();
//                }
//                SortedSet<Integer> cell = elPart.getCell(elCellIndex); 
//                int lastEl = cell.last();
//                discCounters[elCellIndex] = lastEl - (cell.size() - connectedCount) + 1;
//            }
//        }
//        
//        Permutation invertedShort = labelling.invert();
//        Permutation inversion = new Permutation(indexMap.length);
//        for (int index = 0; index < indexMap.length; index++) {
//            int shortIndex = indexMap[index];
//            if (shortIndex == -1) {  // disconnected vertex
//                int elCellIndex = cellIndex(index, elPart);
//                int counter = discCounters[elCellIndex];
//                inversion.set(index, counter);
//                discCounters[elCellIndex]++;
//            } else {
//                int mapped = invertedShort.get(shortIndex);
//                inversion.set(index, mapped);
//            }
//        }
//        return inversion;
//    }
    
    public Partition translate(Partition autPart, Permutation labelling, int[] indexMap) {
        // XXX this method will give an _unordered_ partition!
        // XXX or, at least, it will not be the correct order of cells
        
        Partition translated = new Partition();
        int transCellIndex = 0;
        int prevCellIndex = -1;
        for (int i = 0; i < indexMap.length; i++) {
            int shortIndex = indexMap[i];
            int j = labelling.get(i);
            int currentCellIndex;
            if (shortIndex == -1) {
                currentCellIndex = -1;
            } else {
                currentCellIndex = cellIndex(shortIndex, autPart);
            }
            if (currentCellIndex != -1 &&
                    (prevCellIndex == -1 || prevCellIndex == currentCellIndex)) {
                if (transCellIndex >= translated.size()) {
                    translated.addSingletonCell(j);
                } else {
                    translated.addToCell(transCellIndex, j);
                }
                prevCellIndex = currentCellIndex;
            } else {
                if (currentCellIndex != -1) {
                    prevCellIndex = currentCellIndex;
                }
                transCellIndex++;
                translated.addSingletonCell(j);
            }
//            System.out.println("i: " + i + " j: " + j + " t: " + translated + " c: " + currentCellIndex + " p: " + prevCellIndex);
        }
        
        return translated;
    }
    
    private int cellIndex(int i, Partition p) {
        for (int j = 0; j < p.size(); j++) {
            if (p.getCell(j).contains(i)) {
                return j;
            }
        }
        return -1;
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
