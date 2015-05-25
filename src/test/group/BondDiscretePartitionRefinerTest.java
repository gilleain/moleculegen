package test.group;

import static io.AtomContainerPrinter.fromString;
import io.AtomContainerPrinter;
import group.BondDiscretePartitionRefiner;
import group.Partition;
import group.Permutation;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;



public class BondDiscretePartitionRefinerTest {
    
    public static final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    @Test
    public void testAlleneA() {
        String acpString = "C0C1C2C3 0:1(1),0:2(2),2:3(2)";
        System.out.println(acpString);
        IAtomContainer alleneA = fromString(acpString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        Partition initial = refiner.getBondPartition(alleneA);
//        PermutationGroup group = refiner.getAutomorphismGroup(alleneA, initial);
//        PermutationGroup group = refiner.getAutomorphismGroup(alleneA);
//        for (Permutation p : group.all()) { System.out.println(p); }
        Permutation best = refiner.getBest();
        System.out.println(initial + "\t" + best);
    }
    
    @Test
    public void testAlleneB() {
        String acpString = "C0C1C2C3 0:1(2),0:2(2),1:3(1)";
        System.out.println(acpString);
        IAtomContainer alleneB = fromString(acpString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        Partition initial = refiner.getBondPartition(alleneB);
//        PermutationGroup group = refiner.getAutomorphismGroup(alleneB, initial);
//        PermutationGroup group = refiner.getAutomorphismGroup(alleneB);
//      for (Permutation p : group.all()) { System.out.println(p); }
        Permutation best = refiner.getBest();
        System.out.println(initial + "\t" + best);
    }
    
    @Test
    public void star3VsTriangleTest() {
        String star3String = "C0C1C2C3 0:1(1),0:2(1),0:3(1)";
        String triangleString = "C0C1C2 0:1(1),0:2(1),1:2(1)";
        IAtomContainer star3 = fromString(star3String, builder);
        IAtomContainer triangle = fromString(triangleString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        Partition initialStar3 = refiner.getBondPartition(star3);
        Partition initialTri = refiner.getBondPartition(triangle);
        System.out.println(initialStar3 + "\t" + initialTri);
        refiner.getAutomorphismGroup(star3);
        System.out.println(refiner.getBest());
        refiner.getAutomorphismGroup(triangle);
        System.out.println(refiner.getBest());
    }
    
    @Test
    public void testDisconnected() {
        String discString = "C0C1C2C3C4 0:1(1),0:2(1),1:2(1),3:4(1)";
        IAtomContainer disc = fromString(discString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        refiner.getAutomorphismGroup(disc);
    }

}
