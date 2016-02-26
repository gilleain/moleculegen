package group;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import group.invariant.Invariant;


/**
 * Refines a 'coarse' partition (with more blocks) to a 'finer' partition that
 * is equitable.
 * 
 * Closely follows algorithm 7.5 in CAGES. The basic idea is that the refiner
 * maintains a queue of blocks to refine, starting with all the initial blocks
 * in the partition to refine. These blocks are popped off the queue, and
 * 
 * @author maclean
 * @cdk.module group
 */
public abstract class AbstractEquitablePartitionRefiner {
    
    
    /**
     * A forward split order tends to favor partitions where the cells are
     * refined from lowest to highest. A reverse split order is, of course, the
     * opposite.
     * 
     */
    public enum SplitOrder { FORWARD, REVERSE };
    
    /**
     * The bias in splitting cells when refining
     */
    private SplitOrder splitOrder = SplitOrder.FORWARD;
    
    /**
     * The blocks to be refined, or at least considered for refinement
     */
    private Queue<Set<Integer>> blocksToRefine;
    
    /**
     * Gets from the graph the number of vertices. Abstract to allow different 
     * graph classes to be used (eg: IMolecule or IAtomContainer, etc).
     * 
     * @return the number of vertices
     */
    public abstract int getNumberOfVertices();
    
    /**
     * Find |a &cap; b| - that is, the size of the intersection between a and b.
     * 
     * @param block a set of numbers
     * @param vertexIndex the element to compare
     * @return the size of the intersection
     */
    public abstract Invariant neighboursInBlock(Set<Integer> block, int vertexIndex);

    /**
     * Set the preference for splitting cells.
     * 
     * @param splitOrder either FORWARD or REVERSE
     */
    public void setSplitOrder(SplitOrder splitOrder) {
        this.splitOrder = splitOrder;
    }
    
    /**
     * Refines the coarse partition <code>coarse</code> into a finer one.
     *  
     * @param coarse the partition to refine
     * @return a finer partition
     */
    public Partition refine(Partition coarse) {
        Partition finer = new Partition(coarse);
        
        // start the queue with the blocks of the coarse partition in reverse order
        blocksToRefine = new LinkedList<Set<Integer>>();
        for (int i = 0; i < finer.size(); i++) {
            blocksToRefine.add(finer.copyBlock(i));
        }
        
        int numberOfVertices = getNumberOfVertices();
        while (!blocksToRefine.isEmpty()) {
            Set<Integer> targetBlock = blocksToRefine.remove();
            int currentBlockIndex = 0;
            while (currentBlockIndex < finer.size() && finer.size() < numberOfVertices) {
                if (!finer.isDiscreteCell(currentBlockIndex)) {
                    SortedSet<Integer> currentBlock = finer.getCell(currentBlockIndex);
                    
                    // get the neighbor invariants for this block
                    Map<Invariant, SortedSet<Integer>> invariants = 
                            getInvariants(currentBlock, targetBlock);

                    // split the block on the basis of these invariants
                    if (invariants.size() > 1) {
                        currentBlockIndex = split(invariants, currentBlockIndex, finer);
                    }
                }
                currentBlockIndex++;
            }

            // the partition is discrete
            if (finer.size() == numberOfVertices) {
                return finer;
            }
        }
        return finer;
    }
    
    /**
     * Gets the neighbor invariants for the block j as a map of 
     * |N<sub>g</sub>(v) &cap; T| to elements of the block j. That is, the
     * size of the intersection between the set of neighbors of element v in
     * the graph and the target block T. 
     *  
     * @param partition the current partition
     * @param targetBlock the current target block of the partition
     * @return a map of set intersection sizes to partition cells
     */
    private Map<Invariant, SortedSet<Integer>> getInvariants(
            SortedSet<Integer> currentBlock, Set<Integer> targetBlock) {
        Map<Invariant, SortedSet<Integer>> invariantMap = new HashMap<Invariant, SortedSet<Integer>>();
        for (int element : currentBlock) {
            Invariant invariant = neighboursInBlock(targetBlock, element);
            if (invariantMap.containsKey(invariant)) {
                invariantMap.get(invariant).add(element);
            } else {
                SortedSet<Integer> set = new TreeSet<Integer>();
                set.add(element);
                invariantMap.put(invariant, set);
            }
        }
//        System.out.println("current block " + currentBlock + " target block " + targetBlock + " inv " + invariantMap);
        return invariantMap;
    }
    
    /**
     * Split the current block using the invariants calculated in getInvariants.
     * 
     * @param invariantMap a map of neighbor counts to elements
     * @param partition the partition that is being refined
     */
    private int split(Map<Invariant, SortedSet<Integer>> invariantMap, 
                       int currentBlockIndex, Partition partition) {
        List<Invariant> invariantKeys =  new ArrayList<Invariant>();
        invariantKeys.addAll(invariantMap.keySet());
        if (splitOrder == SplitOrder.REVERSE) {
            sort(invariantKeys);
        } else {
            sort(invariantKeys, reverseOrder());
        }
//        System.out.println(invariantKeys);
        
        partition.removeCell(currentBlockIndex);
        int insertPoint = currentBlockIndex;
        for (Invariant invariant : invariantKeys) {
            SortedSet<Integer> setH = invariantMap.get(invariant);
            partition.insertCell(insertPoint, setH);
            blocksToRefine.add(setH);
            insertPoint++;
        }
        // skip over the newly added blocks
        return currentBlockIndex + invariantMap.keySet().size() - 1;
    }

}
