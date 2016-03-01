package util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import io.AtomContainerPrinter;
import util.molecule.CutCalculator;

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
    
    private void testEdges(String molString, int expectedCount) {
        IAtomContainer mol = AtomContainerPrinter.fromString(molString, builder); 
        List<Integer> indices = CutCalculator.getCutEdges(mol);
        printBonds(indices, mol);
        assertEquals(expectedCount, indices.size());
    }
    
    private void testVertices(String molString, int expectedCount) {
        IAtomContainer mol = AtomContainerPrinter.fromString(molString, builder); 
        List<Integer> indices = CutCalculator.getCutVertices(mol);
        System.out.println(indices);
        assertEquals(expectedCount, indices.size());
    }
    
    @Test
    public void testTree() {
        testEdges("C0C1C2C3C4C5C6 0:1(1),1:2(1),1:3(1),3:5(1),4:5(1),5:6(1)", 2);
    }
    
    @Test
    public void bridgedRings() {
        testEdges("C0C1C2C3C4C5C6 0:1(1),0:2(1),1:2(1),1:3(1),3:4(1),3:5(1),4:6(1),5:6(1)", 1);
    }
    
    @Test
    public void spiraRings() {
        testVertices("C0C1C2C3C4C5C6 0:1(1),0:3(1),1:2(1),2:3(1),3:4(1),3:6(1),4:5(1),5:6(1)", 1);
    }
    
    @Test
    public void testCutVerticesInSingleRing() {
        testVertices("C0C1N2C3 0:1(3),0:2(1),1:2(1),2:3(1)", 1);
    }
    
    @Test
    public void testWonkyBowtie() {
        testEdges("C0C1C2C3C4C5C6 0:1(1),0:2(1),1:3(1),2:4(1),3:5(1),0:4(1),1:5(1),5:6(1)", 1);
    }

}
