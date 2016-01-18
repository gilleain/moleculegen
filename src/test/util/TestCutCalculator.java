package test.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import io.AtomContainerPrinter;
import util.CutCalculator;

public class TestCutCalculator {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    private String bondToString(IAtomContainer mol, IBond bond) {
        return mol.getAtomNumber(bond.getAtom(0)) + ":" +  mol.getAtomNumber(bond.getAtom(1));
    }
    
    private void printBonds(List<Integer> edgeIndices, IAtomContainer mol) {
        for (int edgeIndex : edgeIndices) {
            System.out.println(bondToString(mol, mol.getBond(edgeIndex)));
        }
    }
    
    @Test
    public void testTree() {
        IAtomContainer mol = AtomContainerPrinter.fromString(
                "C0C1C2C3C4C5C6 0:1(1),1:2(1),1:3(1),3:5(1),4:5(1),5:6(1)", builder);
        printBonds(CutCalculator.getCutEdges(mol), mol);
    }
    
    @Test
    public void bridgedRings() {
        IAtomContainer mol = AtomContainerPrinter.fromString(
                "C0C1C2C3C4C5C6 0:1(1),0:2(1),1:2(1),1:3(1),3:4(1),3:5(1),4:6(1),5:6(1)", builder);
        List<Integer> indices = CutCalculator.getCutEdges(mol);
        printBonds(indices, mol);
        assertEquals(1, indices.size());
    }

}
