package test.group;

import group.BondDiscretePartitionRefiner;
import group.Partition;
import group.Permutation;
import group.PermutationGroup;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import test.AtomContainerPrinter;

public class BondDiscretePartitionRefinerTest {
    
    public static final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    @Test
    public void testAlleneA() {
        String acpString = "C0C1C2C3 0:1(1),0:2(2),2:3(2)";
        IAtomContainer alleneA = AtomContainerPrinter.fromString(acpString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        Partition initial = refiner.getBondPartition(alleneA);
        System.out.println(initial);
        PermutationGroup group = refiner.getAutomorphismGroup(alleneA, initial);
        for (Permutation p : group.all()) {
            System.out.println(p);
        }
        Permutation best = refiner.getBest();
        System.out.println(best);
    }
    
    @Test
    public void testAlleneB() {
        String acpString = "C0C1C2C3 0:1(2),0:2(2),1:3(1)";
        IAtomContainer alleneB = AtomContainerPrinter.fromString(acpString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        Partition initial = refiner.getBondPartition(alleneB);
        System.out.println(initial);
        PermutationGroup group = refiner.getAutomorphismGroup(alleneB, initial);
        for (Permutation p : group.all()) {
            System.out.println(p);
        }
        Permutation best = refiner.getBest();
        System.out.println(best);
    }

}
