package test.group;

import group.AtomDiscretePartitionRefiner;
import group.Partition;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class AtomDiscretePartitionRefinerTest {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    public IAtomContainer makeAtomContainer(String elements) {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < elements.length(); i++) {
            String element = String.valueOf(elements.charAt(i));
            ac.addAtom(builder.newInstance(IAtom.class, element));
        }
        return ac;
    }
    
    @Test
    public void oddEvenElementPartitionTest() {
        IAtomContainer ac = makeAtomContainer("CNCNCN");
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        Partition p = refiner.getElementPartition(ac);
        System.out.println(p);
    }
    
    @Test
    public void orderedElementPartitionTest() {
        IAtomContainer ac = makeAtomContainer("CCCCNNNNOOOO");
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        Partition p = refiner.getElementPartition(ac);
        System.out.println(p);
    }

}
