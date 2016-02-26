//package group;
//
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Test;
//import org.openscience.cdk.interfaces.IAtom;
//import org.openscience.cdk.interfaces.IAtomContainer;
//import org.openscience.cdk.interfaces.IChemObjectBuilder;
//import org.openscience.cdk.silent.SilentChemObjectBuilder;
//
//import group.molecule.AtomDiscretePartitionRefiner;
//import io.AtomContainerPrinter;
//
//
//
//public class AtomDiscretePartitionRefinerTest {
//    
//    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
//    
//    public IAtomContainer makeAtomContainer(String elements) {
//        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
//        for (int i = 0; i < elements.length(); i++) {
//            String element = String.valueOf(elements.charAt(i));
//            ac.addAtom(builder.newInstance(IAtom.class, element));
//        }
//        return ac;
//    }
//    
//    @Test
//    public void oddEvenElementPartitionTest() {
//        IAtomContainer ac = makeAtomContainer("CNCNCN");
//        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
//        Partition p = refiner.getElementPartition(ac);
//        System.out.println(p);
//    }
//    
//    @Test
//    public void orderedElementPartitionTest() {
//        IAtomContainer ac = makeAtomContainer("CCCCNNNNOOOO");
//        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
//        Partition p = refiner.getElementPartition(ac);
//        System.out.println(p);
//    }
//    
//    @Test
//    public void isomorphicTest() {
//        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
//        
//        String acpStringA = "C0C1C2C3 0:1(1),0:2(1),1:3(1)";
//        IAtomContainer acA = AtomContainerPrinter.fromString(acpStringA, builder);
//        refiner.getAutomorphismGroup(acA);
//        Permutation bestA = refiner.getBest();
//        refiner.reset();
//        
//        String acpStringB = "C0C1C2C3 0:2(1),1:2(1),0:3(1)";
//        IAtomContainer acB = AtomContainerPrinter.fromString(acpStringB, builder);
//        refiner.getAutomorphismGroup(acB);
//        Permutation bestB = refiner.getBest();
//        
//        System.out.println(bestA + "\t" + bestB);
//    }
//    
//    @Test
//    public void connectedTest() {
//        String acpString = "C0C1O2 0:1(1),0:2(1)";
//        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
//        boolean checkForDisconnected = false;
//        AtomDiscretePartitionRefiner refiner = 
//            new AtomDiscretePartitionRefiner(checkForDisconnected);
//        List<Map<Integer, Integer>> table = refiner.makeConnectionTable(ac);
//        System.out.println(table);
//        refiner.getAutomorphismGroup(ac);
//        Permutation best = refiner.getBest();
//        System.out.println(best);
//    }
//    
//    @Test
//    public void disconnectedAtEndTest() {
////        String acpString = "C0C1O2O3 0:1(1),0:2(1)";
//        String acpString = "C0C1C2 0:1(1)";
//        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
//        boolean checkForDisconnected = true;
//        AtomDiscretePartitionRefiner refiner = 
//            new AtomDiscretePartitionRefiner(checkForDisconnected);
//        List<Map<Integer, Integer>> table = refiner.makeCompactConnectionTable(ac);
//        System.out.println(table);
//        refiner.getAutomorphismGroup(ac);
//        Permutation best = refiner.getBest();
//        System.out.println(best);
//    }
//    
//    @Test
//    public void disconnectedInMiddleTest() {
//        String acpString = "C0C1O2O3 0:1(2),0:3(1)";
//        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
//        boolean checkForDisconnected = true;
//        AtomDiscretePartitionRefiner refiner = 
//            new AtomDiscretePartitionRefiner(checkForDisconnected);
//        
//        List<Map<Integer, Integer>> table = refiner.makeCompactConnectionTable(ac);
//        System.out.println(table);
//        
//        
//        Partition partition = refiner.getElementPartition(ac);
//        System.out.println(partition);
//        
//        refiner.getAutomorphismGroup(ac);
//        
//        Permutation best = refiner.getBest();
//        System.out.println(best);
//    }
//    
//    
//
//}
