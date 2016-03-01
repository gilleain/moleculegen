package group.molecule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import group.Refinable;

public class MoleculeRefinable implements Refinable {
    
    /**
     * If there are atoms in the structure without bonds, this should be true
     */
    private boolean checkForDisconnectedAtoms;
    
    /**
     * Specialised option to allow generating automorphisms that ignore the bond order.
     */
    private boolean ignoreBondOrders;
    
    /**
     * A convenience lookup table for atom-atom connections
     * TODO : less expensive to use Map<Integer, List<Integer>>?
     */
    private List<Map<Integer, Integer>> connectionTable;
    
    /**
     * For compact connection tables, this is necessary to map the original atom indices
     * to the ones used in the compact table.
     * 
     */
    private int[] indexMap;
    
    private int maxBondOrder;
    
    public MoleculeRefinable(IAtomContainer atomContainer) {
        this(atomContainer, false, false);
    }
    
    public MoleculeRefinable(
            IAtomContainer atomContainer,  
            boolean checkForDisconnectedAtoms, 
            boolean ignoreBondOrders) {
        this.checkForDisconnectedAtoms = checkForDisconnectedAtoms;
        this.ignoreBondOrders = ignoreBondOrders;
        setupConnectionTable(atomContainer);
    }
    
    // TODO : put in interface or remove?
    public int[] getIndexMap() {
        return indexMap;
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
    
    @Override
    public int[] getConnectedIndices(int vertexIndex) {
        Set<Integer> connectedSet = connectionTable.get(vertexIndex).keySet(); 
        int[] connected = new int[connectedSet.size()];
        int index = 0;
        for (int element : connectedSet) {
            connected[index] = element;
            index++;
        }
        return connected;
    }

    @Override
    public int getVertexCount() {
        return connectionTable.size();
    }

    @Override
    public int getMaxConnectivity() {
        return maxBondOrder;
    }

    private void setupConnectionTable(IAtomContainer atomContainer) {
        if (checkForDisconnectedAtoms) {
            this.connectionTable = makeCompactConnectionTable(atomContainer);
        } else {
            this.connectionTable = makeConnectionTable(atomContainer);
        }
    }
    
    /**
     * Makes a lookup table for the connection between atoms, to avoid looking
     * through the bonds each time.
     * 
     * @return a connection table of atom indices to connected atom indices
     */
    private List<Map<Integer, Integer>> makeConnectionTable(IAtomContainer atomContainer) {
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
                    int bondOrder = bondOrder(bond.getOrder());
                    connectedIndices.put(index, bondOrder);
                    if (bondOrder > maxBondOrder) {
                        maxBondOrder = bondOrder;
                    }
                }
            }
            table.add(connectedIndices);
        }
        return table;
    }
    
    /**
     * Alternate connection table that ignores atoms with no connections.
     * 
     * @return
     */
    private List<Map<Integer, Integer>> makeCompactConnectionTable(IAtomContainer atomContainer) {
        List<Map<Integer, Integer>> table = new ArrayList<Map<Integer, Integer>>();
        
        // make the index map anew
        indexMap = new int[atomContainer.getAtomCount()];
        
        int tableIndex = 0;
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
                        int bondOrder = bondOrder(bond.getOrder());
                        connectedIndices.put(index, bondOrder);
                        if (bondOrder > maxBondOrder) {
                            maxBondOrder = bondOrder;
                        }
                    }
                }
                table.add(connectedIndices);
                indexMap[i] = tableIndex;
                tableIndex++;
            } else {
                indexMap[i] = -1;
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
    
    private int bondOrder(IBond.Order order) {
        switch (order) {
            case SINGLE: return 1;
            case DOUBLE: return 2;
            case TRIPLE: return 3;
            default: return 4;
        }
    }

}
