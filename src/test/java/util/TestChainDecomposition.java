package util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import io.AtomContainerPrinter;
import util.molecule.ChainDecomposition;

public class TestChainDecomposition {
    
    private IChemObjectBuilder builder =  SilentChemObjectBuilder.getInstance();
    
    private void printBridges(IAtomContainer mol, ChainDecomposition chainDecomposition) {
        for (IBond bond : chainDecomposition.getBridges()) {
            System.out.println(bondToString(mol, bond));
        }
    }
    
    private void printCycles(IAtomContainer mol, ChainDecomposition chainDecomposition) {
        int count = 1;
        for (List<IBond> cycle : chainDecomposition.getCycleChains()) {
            System.out.print(count + " ");
            for (IBond bond : cycle) {
                System.out.print(bondToString(mol, bond) + ",");
            }
            System.out.println();
            count++;
        }
    }
    
    private String bondToString(IAtomContainer mol, IBond bond) {
        return mol.getAtomNumber(bond.getAtom(0)) + ":" +  mol.getAtomNumber(bond.getAtom(1));
    }
    
    @Test
    public void spira() {
        IAtomContainer mol = AtomContainerPrinter.fromString(
                "C0C1C2C3C4C5C6 0:1(1),0:3(1),1:2(1),2:3(1),3:4(1),3:6(1),4:5(1),5:6(1)", builder);
        ChainDecomposition chainDecomposition = new ChainDecomposition(mol);
        assertEquals(0, chainDecomposition.getBridges().size());
        printCycles(mol, chainDecomposition);
    }
    
    @Test
    public void bridgedRings() {
        IAtomContainer mol = AtomContainerPrinter.fromString(
                "C0C1C2C3C4C5C6 0:1(1),0:2(1),1:2(1),1:3(1),3:4(1),3:5(1),4:6(1),5:6(1)", builder);
        ChainDecomposition chainDecomposition = new ChainDecomposition(mol);
        assertEquals(1, chainDecomposition.getBridges().size());
        printCycles(mol, chainDecomposition);
        printBridges(mol, chainDecomposition);
    }
    
    @Test
    public void fourLine() {
        IAtomContainer mol = 
                AtomContainerPrinter.fromString("C0C1C2C3 0:1(1),1:2(1),2:3(1)", builder);
        ChainDecomposition chainDecomposition = new ChainDecomposition(mol);
        printBridges(mol, chainDecomposition);
    }

}
