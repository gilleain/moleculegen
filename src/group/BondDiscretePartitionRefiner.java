package group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Determine the automorphism group of the bonds for an atom container.
 * 
 * If two bonds are equivalent under an automorphism in the group, then
 * roughly speaking they are in symmetric positions in the molecule. For
 * example, two methyls attached to a benzene ring are 'equivalent' in 
 * some sense. 
 * 
 * @author maclean
 *
 */
public class BondDiscretePartitionRefiner extends AbstractDiscretePartitionRefiner {
    
    /**
     * The connectivity between bonds; two bonds are connected 
     * if they share an atom.
     */
    private Map<Integer, List<Integer>> connectionTable;
    
    /**
     * Specialised option to allow generating automorphisms that ignore the bond order.
     */
    private boolean ignoreBondOrders;
    
    public BondDiscretePartitionRefiner() {
        this(false);
    }
    
    public BondDiscretePartitionRefiner(boolean ignoreBondOrders) {
        this.ignoreBondOrders = ignoreBondOrders;
    }
    
    /**
     * Gets the automorphism group of the atom container. By default it uses an
     * initial partition based on the bond 'types' (so all the C-C bonds are in
     * one cell, all the C=N in another, etc). If this behaviour is not 
     * desired, then use the {@link ignoreBondOrders} flag in the constructor.
     * 
     * @param atomContainer the atom container to use
     * @return the automorphism group of the atom container
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer) {
        setup(atomContainer);
        Partition initial;
        if (ignoreBondOrders) {
            initial = Partition.unit(getVertexCount());
        } else {
            initial = getBondPartition(atomContainer);
        }
        
        super.refine(initial);
        return super.getGroup();
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
        
        Partition initial;
        if (ignoreBondOrders) {
            initial = Partition.unit(getVertexCount());
        } else {
            initial = getBondPartition(atomContainer);
        }
        
        super.refine(initial);
        return getGroup();
    }
    
    /**
     * Get the automorphism group of the molecule given an initial partition.
     * 
     * @param atomContainer the atom container to use
     * @param initialPartiton an initial partition of the bonds
     * @return the automorphism group starting with this partition
     */
    public PermutationGroup getAutomorphismGroup(
            IAtomContainer atomContainer, Partition initialPartiton) {
        setup(atomContainer);
        refine(initialPartiton);
        return getGroup();
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
    
    public void refine(Partition p, IAtomContainer container) {
        setup(container);
        refine(p);
    }
    
    /**
     * Get the bond partition, based on the element types of the atoms at either end
     * of the bond, and the bond order.
     * 
     * @param atomContainer the container with the bonds to partition
     * @return a partition of the bonds based on the element types and bond order
     */
    public Partition getBondPartition(IAtomContainer atomContainer) {
        int bondCount = atomContainer.getBondCount();
        Map<String, SortedSet<Integer>> cellMap = new HashMap<String, SortedSet<Integer>>();
        
        // make mini-'descriptors' for bonds like "C=O" or "C#N" etc
        for (int bondIndex = 0; bondIndex < bondCount; bondIndex++) {
            IBond bond = atomContainer.getBond(bondIndex);
            String el0 = bond.getAtom(0).getSymbol();
            String el1 = bond.getAtom(1).getSymbol();
            String boS = bondOrderString(bond.getOrder());
            String bondString;
            if (el0.compareTo(el1) < 0) {
                bondString = el0 + boS + el1;
            } else {
                bondString = el1 + boS + el0;                
            }
            SortedSet<Integer> cell;
            if (cellMap.containsKey(bondString)) {
                cell = cellMap.get(bondString);
            } else {
                cell = new TreeSet<Integer>();
                cellMap.put(bondString, cell);
            }
            cell.add(bondIndex);
        }
        
        // sorting is necessary to get cells in order
        List<String> bondStrings = new ArrayList<String>(cellMap.keySet());
        Collections.sort(bondStrings);
        
        // the partition of the bonds by these 'descriptors'
        Partition bondPartition = new Partition();
        for (String key : bondStrings) {
            SortedSet<Integer> cell = cellMap.get(key);
            bondPartition.addCell(cell);
        }
//        System.out.println(bondStrings);
//        bondPartition.order();
        return bondPartition;
    }
    
    private String bondOrderString(IBond.Order order) {
        switch (order) {
            case SINGLE: return "1";
            case DOUBLE: return "2";
            case TRIPLE: return "3";
            default:     return "4";    // XXX? 4
        }
    }

    private void setup(IAtomContainer atomContainer) {
        // have to setup the connection table before making the group 
        // otherwise the size may be wrong
        setupConnectionTable(atomContainer);
        
        int n = getVertexCount();
        PermutationGroup group = new PermutationGroup(new Permutation(n));
        setup(group, new BondEquitablePartitionRefiner(connectionTable));
    }
    
    private void setup(IAtomContainer atomContainer, PermutationGroup group) {
        setupConnectionTable(atomContainer);
        setup(group, new BondEquitablePartitionRefiner(connectionTable));
    }
    
    private void setupConnectionTable(IAtomContainer atomContainer) {
        connectionTable = new HashMap<Integer, List<Integer>>();
        int bondCount = atomContainer.getBondCount();
        for (int bondIndexI = 0; bondIndexI < bondCount; bondIndexI++) {
            if (!connectionTable.containsKey(bondIndexI)) {
                connectionTable.put(bondIndexI, new ArrayList<Integer>());
            }
            IBond bondI = atomContainer.getBond(bondIndexI);
            for (int bondIndexJ = 0; bondIndexJ < bondCount; bondIndexJ++) {
                if (bondIndexI == bondIndexJ) continue;
                IBond bondJ = atomContainer.getBond(bondIndexJ);
                if (bondI.isConnectedTo(bondJ)) {
                    connectionTable.get(bondIndexI).add(bondIndexJ);
                }
            }
        }
    }

    @Override
    public int getVertexCount() {
        return connectionTable.size();
    }

    @Override
    public int getConnectivity(int i, int j) {
        if (connectionTable.containsKey(i) &&
            connectionTable.get(i).contains(j)) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean colorsPreserved(Permutation p) {
        // TODO Auto-generated method stub
        return false;
    }
}
