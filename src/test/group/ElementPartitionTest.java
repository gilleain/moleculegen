package test.group;

import io.AtomContainerPrinter;
import junit.framework.Assert;
import group.AtomDiscretePartitionRefiner;
import group.Partition;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;



public class ElementPartitionTest {
    
    private static final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    @Test
    public void c3O3Test() {
        Partition expected = Partition.fromString("0,1,2|3,4,5");
        String acp = "C0C1O2O3O4C5 0:1(1),1:2(1),0:2(1),0:3(1),3:4(1),4:5(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acp, builder);
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        Partition actual = refiner.getElementPartition(ac);
        Assert.assertEquals(expected, actual);
    }

}
