package group;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.FastChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import group.molecule.AtomDiscretePartitionRefiner;
import io.AtomContainerPrinter;



public class AtomDiscretePartitionRefinerTest {
    
    private IChemObjectBuilder builder =  FastChemObjectBuilder.getInstance();   // changed SLewis for control
    
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
    
    @Test
    public void isomorphicTest() {
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        
        String acpStringA = "C0C1C2C3 0:1(1),0:2(1),1:3(1)";
        IAtomContainer acA = AtomContainerPrinter.fromString(acpStringA, builder);
        refiner.getAutomorphismGroup(acA);
        Permutation bestA = refiner.getBest();
        refiner.reset();
        
        String acpStringB = "C0C1C2C3 0:2(1),1:2(1),0:3(1)";
        IAtomContainer acB = AtomContainerPrinter.fromString(acpStringB, builder);
        refiner.getAutomorphismGroup(acB);
        Permutation bestB = refiner.getBest();
        
        System.out.println(bestA + "\t" + bestB);
    }

}
