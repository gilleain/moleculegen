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
    
    private boolean useBondOrders;
    
    private boolean useElementColors;
    
    private boolean useBondColors;
    
    private String[] elementColors;
    
    /**
     * 
     */
    private Map<Integer, List<Integer>> bondColorTable;
    
    public AtomDiscretePartitionRefiner() {
        this(false, true);
    }
    
    public AtomDiscretePartitionRefiner(boolean checkForDisconnectedAtoms) {
        this(checkForDisconnectedAtoms, false, false, false);
    }
    
    public AtomDiscretePartitionRefiner(
            boolean checkForDisconnectedAtoms, boolean useBondOrders) {
        this(checkForDisconnectedAtoms, useBondOrders, false, false);
    }
    
    public AtomDiscretePartitionRefiner(
            boolean checkForDisconnectedAtoms, 
            boolean useBondOrders,
            boolean useElementColors,
            boolean useBondColors) {
//        super(useElementColors, useBondColors);
        this.checkForDisconnectedAtoms = checkForDisconnectedAtoms;
        this.useBondOrders = useBondOrders;
        this.useElementColors = useElementColors;
        this.useBondColors = useBondColors;
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
                IBond bond = atomContainer.getBond(a, connected);
                if (useBondOrders) {
                    connectedIndices.put(index, bondOrder(bond.getOrder()));
                } else {
                    connectedIndices.put(index, 1);
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
                    IBond bond = atomContainer.getBond(atom, connectedAtom);
                    connectedIndices.put(index, bondOrder(bond.getOrder()));
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
    
    // TODO : compact bond color table?
    private Map<Integer, List<Integer>> makeBondColorTable(IAtomContainer atomContainer) {
        Map<Integer, List<Integer>> table = new HashMap<Integer, List<Integer>>();
        for (int index = 0; index < atomContainer.getAtomCount(); index++) {
            IAtom atom = atomContainer.getAtom(index);
            List<Integer> bondColors = new ArrayList<Integer>();
            for (IBond bond : atomContainer.getConnectedBondsList(atom)) {
                bondColors.add(convertBondOrder(bond.getOrder()));
            }
            table.put(index, bondColors);
        }
        
        return table;
    }
    
    private int convertBondOrder(IBond.Order o) {
        return o.ordinal();
    }
    
    private void setup(IAtomContainer atomContainer) {
        if (checkForDisconnectedAtoms) {
            this.connectionTable = makeCompactConnectionTable(atomContainer);
        } else {
            this.connectionTable = makeConnectionTable(atomContainer);
        }
        if (useElementColors) {
            // TODO : do this at the same time as creating adj matrix..
            elementColors = new String[atomContainer.getAtomCount()];
            for (int i = 0; i < atomContainer.getAtomCount(); i++) {
                elementColors[i] = atomContainer.getAtom(i).getSymbol();
            }
        }
        if (useBondColors) {
            // TODO : do this at the same time as creating adj matrix..
            bondColorTable = makeBondColorTable(atomContainer);
        }
        int n = getVertexCount();
        PermutationGroup group = new PermutationGroup(new Permutation(n));
        IEquitablePartitionRefiner refiner = 
            new CDKEquitablePartitionRefiner(connectionTable, useBondOrders);
        setup(group, refiner);
    }
    
    private void setup(IAtomContainer atomContainer, PermutationGroup group) {
        // TODO : error XXX! connectionTable will be null FIXME
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
//        refine(Partition.unit(getVertexCount()));
//        return firstIsIdentity();
        return isCanonical();
    }
    
    /**
     * Gets the automorphism group of the atom container.
     * 
     * @param atomContainer the atom container to use
     * @return the automorphism group of the atom container
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer) {
        setup(atomContainer);
        int n = getVertexCount();
        Partition unit = Partition.unit(n);
        refine(unit);
        return getGroup();
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
     * @param atomContainer
     * @param initialPartiton
     * @return
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
    public int isConnected(int i, int j) {
        if (connectionTable.get(i).containsKey(j)) {
            return connectionTable.get(i).get(j);
        } else {
            return 0;
        }
    }
    
    public boolean sameColor(int i, int j) {
        return elementColors[i] == elementColors[j];
    }

//    @Override
//    public boolean sameVertexColor(int i, int j) {
//        return elementColors[i] == elementColors[j];
//    }
//
//    @Override
//    public boolean sameEdgeColor(int iOld, int jOld, int iNew, int jNew) {
//        int oldBondColor = bondColorTable.get(iOld).get(jOld);
//        int newBondColor = bondColorTable.get(iNew).get(jNew);
//        return oldBondColor == newBondColor;
//    }

}
