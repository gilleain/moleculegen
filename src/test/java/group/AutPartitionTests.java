package group;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.signature.Orbit;
import org.openscience.cdk.silent.FastChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import io.AtomContainerPrinter;

public class AutPartitionTests {
    
    public static IChemObjectBuilder getBuilder() {
        return  FastChemObjectBuilder.getInstance();   // changed SLewis for control
    }

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
            new IteratingSMILESReader(new FileReader(filename), getBuilder());
        
        while (reader.hasNext()) {
            IAtomContainer ac = reader.next();
            sort(ac);
            MoleculeSignature molSig = new MoleculeSignature(ac);
            List<Orbit> signatureOrbits = molSig.calculateOrbits();
            AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
            PermutationGroup group = refiner.getAutomorphismGroup(ac);
            Partition autPartition = refiner.getAutomorphismPartition();
            String pS = simplify(signatureOrbits).toString();
            String aS = autPartition.toString();
            if (!pS.equals(aS)) {
                String acp = AtomContainerPrinter.toString(ac);
                System.out.println(pS + "\t" + aS + "\t" + group.order() + "\t" + acp);
            }
        }
        reader.close();
    }
    
    private Partition simplify(List<Orbit> orbits) {
        Partition p = new Partition();
        for (Orbit o : orbits) {
            p.addCell(o.getAtomIndices());
        }
        return p;
    }
    
    public void testDisconnectedStructure(String acp) {
        IAtomContainer ac = AtomContainerPrinter.fromString(acp, getBuilder());
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner(true);
        PermutationGroup group = refiner.getAutomorphismGroup(ac);
        Partition autPartition = refiner.getAutomorphismPartition();
        System.out.println(acp + "\t" + group.order() + "\t" + autPartition);
        for (Permutation p : group.all()) {
            System.out.println(p);
        }
    }
    
    @Test
    public void CEdge_CDot_OEdge_Test() {
        testDisconnectedStructure("C0C1C2O3O4 0:1(1),3:4(1)");
    }

    @Test
    public void CEdge_CDot_OEdge_ODotTest() {
        testDisconnectedStructure("C0C1C2O3O4O5 0:1(1),3:4(1)");
    }

    @Test
    public void C_Dot_CEdge_ODot_OEdgeTest() {
        testDisconnectedStructure("C0C1C2O3O4O5 1:2(1),4:5(1)");
    }

    @Test
    public void CEdge_OEdgeTest() {
        testDisconnectedStructure("C0C1O2O3 0:1(1),2:3(1)");
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
