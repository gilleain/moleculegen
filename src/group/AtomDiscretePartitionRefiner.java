package group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * A refiner for CDK atom containers; see: 
 * {@link AbstractDiscretePartitionRefiner}.
 *  
 * @author maclean
 * @cdk.module group
 */
public class AtomDiscretePartitionRefiner extends AbstractDiscretePartitionRefiner {
    
    /**
     * A convenience lookup table for atom-atom connections
     * TODO : less expensive to use Map<Integer, List<Integer>>?
     */
    private List<Map<Integer, Integer>> connectionTable;
    
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
        this.checkForDisconnectedAtoms = checkForDisconnectedAtoms;
        this.ignoreElements = ignoreElements;
        this.ignoreBondOrders = ignoreBondOrders;
    }
    
    /**
     * Makes a lookup table for the connection between atoms, to avoid looking
     * through the bonds each time.
     * 
     * @return a connection table of atom indices to connected atom indices
     */
    public List<Map<Integer, Integer>> makeConnectionTable(IAtomContainer atomContainer) {
        List<Map<Integer, Integer>> table = new ArrayList<Map<Integer, Integer>>();
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            IAtom a = atomContainer.getAtom(i);
            Map<Integer, Integer> connectedIndices = new HashMap<Integer, Integer>();
            for (IAtom connected : atomContainer.getConnectedAtomsList(a)) {
                int index = atomContainer.getAtomNumber(connected);
                if (ignoreBondOrders) {
                    connectedIndices.put(index, 1);
                } else {
                    IBond bond = atomContainer.getBond(a, connected);
                    connectedIndices.put(index, bondOrder(bond.getOrder()));
                }
            }
            table.add(connectedIndices);
        }
        return table;
    }
    
    private int bondOrder(IBond.Order order) {
        switch (order) {
            case SINGLE: return 1;
            case DOUBLE: return 2;
            case TRIPLE: return 3;
            default: return 4;
        }
    }
    
    /**
     * Alternate connection table that ignores atoms with no connections.
     * 
     * @return
     */
    public List<Map<Integer, Integer>> makeCompactConnectionTable(IAtomContainer atomContainer) {
        List<Map<Integer, Integer>> table = new ArrayList<Map<Integer, Integer>>();
        int tableIndex = 0;
        int[] indexMap = new int[atomContainer.getAtomCount()];
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            IAtom atom = atomContainer.getAtom(i);
            List<IAtom> connected = atomContainer.getConnectedAtomsList(atom);
            if (connected.size() > 0) {
                Map<Integer, Integer> connectedIndices = new HashMap<Integer, Integer>();
                for (IAtom connectedAtom : connected) {
                    int index = atomContainer.getAtomNumber(connectedAtom);
                    if (ignoreBondOrders) {
                        connectedIndices.put(index, 1);
                    } else {
                        IBond bond = atomContainer.getBond(atom, connectedAtom);
                        connectedIndices.put(index, bondOrder(bond.getOrder()));
                    }
                }
                table.add(connectedIndices);
                indexMap[i] = tableIndex;
                tableIndex++;
            }
        }
        List<Map<Integer, Integer>> shortTable =  new ArrayList<Map<Integer, Integer>>();
        for (int i = 0; i < table.size(); i++) {
            Map<Integer, Integer> originalConnections = table.get(i);
            Map<Integer, Integer> mappedConnections = new HashMap<Integer, Integer>();
            for (int j : originalConnections.keySet()) {
                mappedConnections.put(indexMap[j], originalConnections.get(j));
            }
            shortTable.add(mappedConnections);
        }
        return shortTable;
    }
    
    private void setupConnectionTable(IAtomContainer atomContainer) {
        if (checkForDisconnectedAtoms) {
            this.connectionTable = makeCompactConnectionTable(atomContainer);
        } else {
            this.connectionTable = makeConnectionTable(atomContainer);
        }
    }
    
    private void setup(IAtomContainer atomContainer) {
        setupConnectionTable(atomContainer);
       
        int n = getVertexCount();
        PermutationGroup group = new PermutationGroup(new Permutation(n));
        IEquitablePartitionRefiner refiner = 
            new CDKEquitablePartitionRefiner(connectionTable, ignoreBondOrders);
        setup(group, refiner);
    }
    
    private void setup(IAtomContainer atomContainer, PermutationGroup group) {
        setupConnectionTable(atomContainer);
        IEquitablePartitionRefiner refiner = 
            new CDKEquitablePartitionRefiner(connectionTable);
        setup(group, refiner);
    }
    
    public void refine(Partition p, IAtomContainer container) {
        setup(container);
//        System.out.println("refining " + p);
        refine(p);
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
        Partition elementPartition = new Partition();
        // the element symbols in order of discovery, for 
        List<String> elementList = new ArrayList<String>();
        for (int atomIndex = 0; atomIndex < atomContainer.getAtomCount(); atomIndex++) {
            String elementSymbol = atomContainer.getAtom(atomIndex).getSymbol();
            int cellIndex = elementList.indexOf(elementSymbol);
            if (cellIndex == -1) {
                cellIndex = elementList.size();
                elementList.add(elementSymbol);
                elementPartition.addSingletonCell(atomIndex);
            } else {
                elementPartition.addToCell(cellIndex, atomIndex);
            }
        }
        
        return elementPartition;
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
        return connectionTable.size();
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.group.AbstractDiscretePartitionRefiner#isConnected(int, int)
     */
    @Override
    public int getConnectivity(int i, int j) {
        if (connectionTable.get(i).containsKey(j)) {
            return connectionTable.get(i).get(j);
        } else {
            return 0;
        }
    }

}
