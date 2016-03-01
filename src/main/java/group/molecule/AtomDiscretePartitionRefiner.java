package group.molecule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openscience.cdk.interfaces.IAtomContainer;

import group.AbstractDiscretePartitionRefiner;
import group.Partition;
import group.Permutation;
import group.PermutationGroup;

/**
 * A refiner for CDK atom containers; see: 
 * {@link AbstractDiscretePartitionRefiner}.
 *  
 * @author maclean
 * @cdk.module group
 */
public class AtomDiscretePartitionRefiner extends AbstractDiscretePartitionRefiner {
    
    // TODO : use interface
    private MoleculeRefinable refinable;
   
    
    /**
     * If there are atoms in the structure without bonds, this should be true
     */
    private boolean checkForDisconnectedAtoms;
    
    /**
     * Specialised option to allow generating automorphisms that ignore the element symbols.
     */
    private boolean ignoreElements;
    
    /**
     * Specialised option to allow generating automorphisms that ignore the bond order.
     */
    private boolean ignoreBondOrders;
    
    /**
     * The vertex colors
     */
    private Partition colors;
    
    /**
     * Default constructor - does not check for disconnected atoms, ignore elements
     * or bond orders.
     */
    public AtomDiscretePartitionRefiner() {
        this(false, false, false);
    }
    
    /**
     * Make a refiner that checks for atoms without bonds.
     * 
     * @param checkForDisconnectedAtoms if true, check for disconnected atoms
     */
    public AtomDiscretePartitionRefiner(boolean checkForDisconnectedAtoms) {
        this(checkForDisconnectedAtoms, false, false);
    }
    
    /**
     * Make a refiner with various advanced options.
     * 
     * @param checkForDisconnectedAtoms if true, check for disconnected atoms
     * @param ignoreElements if true, ignore element symbols when making automorphisms
     * @param ignoreBondOrders if true, ignore bond order when making automorphisms
     */
    public AtomDiscretePartitionRefiner(
            boolean checkForDisconnectedAtoms, 
            boolean ignoreElements,
            boolean ignoreBondOrders) {
        // if we are ignoring elements, we are not preserving colors and v.v.
        super(!ignoreElements);
        
        this.checkForDisconnectedAtoms = checkForDisconnectedAtoms;
        this.ignoreElements = ignoreElements;
        this.ignoreBondOrders = ignoreBondOrders;
    }
    
    public void reset() {
        refinable = null;
    }
    
    
    public int[] getIndexMap() {
        return refinable.getIndexMap();
    }
    
    private void setup(IAtomContainer atomContainer) {
        // have to setup the connection table before making the group 
        // otherwise the size may be wrong, but only setup if it doesn't exist
        if (refinable == null) {
            refinable = new MoleculeRefinable(atomContainer, checkForDisconnectedAtoms, ignoreBondOrders);
        }
        int size = getVertexCount();
        PermutationGroup group = new PermutationGroup(new Permutation(size));
        setup(group, new AtomEquitablePartitionRefiner(refinable));
    }
    
    private void setup(IAtomContainer atomContainer, PermutationGroup group) {
        refinable = new MoleculeRefinable(atomContainer, checkForDisconnectedAtoms, ignoreBondOrders);
        setup(group, new AtomEquitablePartitionRefiner(refinable));
    }
    
    /**
     * Refine an atom partition based on the connectivity in the atom container.
     * 
     * @param partition the initial partition of the atoms
     * @param container the atom container to use
     */
    public void refine(Partition partition, IAtomContainer container) {
        setup(container);
        refine(partition);
    }

    /**
     * Checks if the atom container is canonical.
     * 
     * @param atomContainer the atom container to check
     * @return true if the atom container is canonical
     */
    public boolean isCanonical(IAtomContainer atomContainer) {
        setup(atomContainer);
        return isCanonical();
    }
    
    /**
     * Gets the automorphism group of the atom container. By default it uses an
     * initial partition based on the element symbols (so all the carbons are in
     * one cell, all the nitrogens in another, etc). If this behaviour is not 
     * desired, then use the {@link ignoreElements} flag in the constructor.
     * 
     * @param atomContainer the atom container to use
     * @return the automorphism group of the atom container
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer) {
        setup(atomContainer);
        Partition initial;
        if (ignoreElements) {
            int n = getVertexCount();
            initial = Partition.unit(n);
        } else {
            initial = getElementPartition(atomContainer);
        }
        super.refine(initial);
        return super.getGroup();
    }
    
    /**
     * Get the element partition from an atom container, which is simply a list
     * of sets of atom indices where all atoms in one set have the same element
     * symbol.
     * 
     * So for atoms C0,N1,C2,P3,C4,N5 the partition would be [{0, 2, 4}, {1, 5}, {3}]
     * with cells for elements C, N, and P.
     *  
     * @param atomContainer the atom container to get element symbols from
     * @return a partition of the atom indices based on the element symbols
     */
    public Partition getElementPartition(IAtomContainer atomContainer) {
        if (refinable == null) {
            refinable = new MoleculeRefinable(atomContainer, checkForDisconnectedAtoms, ignoreBondOrders);
        }
        
        Map<String, SortedSet<Integer>> cellMap = new HashMap<String, SortedSet<Integer>>();
        int numberOfAtoms = atomContainer.getAtomCount(); 
        for (int atomIndex = 0; atomIndex < numberOfAtoms; atomIndex++) {
            int index = (refinable.getIndexMap() == null)? atomIndex : refinable.getIndexMap()[atomIndex];
            if (index >= 0) {
                String symbol = atomContainer.getAtom(atomIndex).getSymbol();
                SortedSet<Integer> cell;
                if (cellMap.containsKey(symbol)) {
                    cell = cellMap.get(symbol);
                } else {
                    cell = new TreeSet<Integer>();
                    cellMap.put(symbol, cell);
                }
                cell.add(index);
            }
        }
        
        List<String> atomSymbols = new ArrayList<String>(cellMap.keySet());
        Collections.sort(atomSymbols);
        
        Partition elementPartition = new Partition();
        for (String key : atomSymbols) {
            SortedSet<Integer> cell = cellMap.get(key);
            elementPartition.addCell(cell);
        }
        
//        this.colors = elementPartition;
        
        return elementPartition;
    }
    
    public void setElementPartition(Partition elementPartition) {
        this.colors = elementPartition;
    }
    
    /**
     * Speed up the search for the automorphism group using the automorphisms in
     * the supplied group. Note that the behaviour of this method is unknown if
     * the group does not contain automorphisms...
     * 
     * @param atomContainer the atom container to use
     * @param group the group of known automorphisms
     * @return the full automorphism group
     */
    public PermutationGroup getAutomorphismGroup(
            IAtomContainer atomContainer, PermutationGroup group) {
        setup(atomContainer, group);
        refine(Partition.unit(getVertexCount()));
        return getGroup();
    }
    
    /**
     * Get the automorphism group of the molecule given an initial partition.
     * 
     * @param atomContainer the atom container to use
     * @param initialPartiton an initial partition of the atoms
     * @return the automorphism group starting with this partition
     */
    public PermutationGroup getAutomorphismGroup(
            IAtomContainer atomContainer, Partition initialPartiton) {
        setup(atomContainer);
        refine(initialPartiton);
        return getGroup();
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.group.AbstractDiscretePartitionRefiner#getVertexCount()
     */
    @Override
    public int getVertexCount() {
        return refinable.getVertexCount();
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.group.AbstractDiscretePartitionRefiner#isConnected(int, int)
     */
    @Override
    public int getConnectivity(int i, int j) {
        return refinable.getConnectivity(i, j);
    }

    @Override
    public boolean colorsPreserved(Permutation p) {
        if (colors != null) {
//            String cStr = colors.toString();
//            String pStr = p.toCycleString();
//            String format = "%" + cStr.length() + "s\t%" + cStr.length() + "s"; 
//            System.out.print(String.format(format, cStr, pStr));
            for (int i = 0; i < getVertexCount(); i++) {
                if (inSameCell(i, p.get(i), colors)) {
                    continue;
                } else {
//                    System.out.println(" false");
                    return false;
                }
            }
//            System.out.println(" true");
            return true;
        } else {
//            System.out.println("colors null, so true");
            return true;    // XXX - what if colors are null accidentally?
        }
    }
    
    // TODO : make this a partition method
    private boolean inSameCell(int i, int j, Partition p) {
        for (int c = 0; c < p.size(); c++) {
            SortedSet<Integer> cell = p.getCell(c); 
            if (cell.contains(i) && cell.contains(j)) {
                return true;
            }
        }
        return false;
    }

}
