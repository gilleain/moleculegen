package test.group;

import group.BondDiscretePartitionRefiner;
import group.Partition;
import group.PermutationGroup;

import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import test.AtomContainerPrinter;

public class BondAutPartitionTests {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    public void sort(IAtomContainer ac) {
        for (IBond bond : ac.bonds()) {
            IAtom a0 = bond.getAtom(0);
            IAtom a1 = bond.getAtom(1);
            if (ac.getAtomNumber(a0) > ac.getAtomNumber(a1)) {
                bond.setAtom(a1, 0);
                bond.setAtom(a0, 1);
            }
        }
    }
    
    public void testFile(String filename) throws IOException {
        IIteratingChemObjectReader<IAtomContainer> reader = 
            new IteratingSMILESReader(new FileReader(filename), builder);
//        SmilesGenerator smilesGen = new SmilesGenerator();
        while (reader.hasNext()) {
            IAtomContainer ac = reader.next();
            sort(ac);
            BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
            Partition initial = refiner.getBondPartition(ac);
            PermutationGroup group = refiner.getAutomorphismGroup(ac, initial);
            Partition autPartition = refiner.getAutomorphismPartition();
            String aS = autPartition.toString();
            String acp = AtomContainerPrinter.toString(ac);
//            String smiles = smilesGen.createSMILES(ac);
//            System.out.println(smiles + "\t" + aS + "\t" + group.order() + "\t" + initial + "\t" + acp);
            System.out.println(String.format("%-18s\t%s\t%-18s\t%s", aS, group.order(), initial, acp));
        }
        reader.close();
    }
    
    @Test
    public void testC4H6O() throws IOException {
        testFile("output/C4H6O.smi");
    }
    
    @Test
    public void testC6H8() throws IOException {
        testFile("output/C6H8.smi");
    }
    
    @Test
    public void testC3H4O4() throws IOException {
        testFile("output/C3H4O4.smi");
    }

}
