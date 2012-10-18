package test.group;

import group.AtomDiscretePartitionRefiner;
import group.Permutation;
import group.PermutationGroup;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class PermGroupTest {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    private IAtomContainer getAtomContainer(String elements, int[][] bonds) {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class); 
        for (int i = 0; i < elements.length(); i++) {
            String eS = String.valueOf(elements.charAt(i));
            ac.addAtom(builder.newInstance(IAtom.class, eS));
        }
        for (int[] bond : bonds) {
            int o = bond[2];
            ac.addBond(bond[0], bond[1], IBond.Order.values()[o - 1]);
        }
        return ac;
    }
    
    @Test
    public void pent_1_3_diene() {
        int[][] b = new int[][] { { 0, 1, 2 }, { 0, 2, 1 }, { 1, 4, 1 }, { 2, 3, 2 } };
        IAtomContainer pent12diene = getAtomContainer("CCCCC", b);
        AtomDiscretePartitionRefiner refiner = 
            new AtomDiscretePartitionRefiner(false, true, false, false);
        PermutationGroup group = refiner.getAutomorphismGroup(pent12diene);
        String bhms = refiner.getBestHalfMatrixString();
        System.out.println(bhms);
        for (Permutation p : group.all()) {
            System.out.println(p);
        }
    }

}
