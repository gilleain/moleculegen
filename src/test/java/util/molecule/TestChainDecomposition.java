package util.molecule;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import io.AtomContainerPrinter;

public class TestChainDecomposition {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    private void printBridges(IAtomContainer mol, ChainDecomposition chainDecomposition) {
        int count = 1;
        for (IBond bond : chainDecomposition.getBridges()) {
            System.out.println("Bridge " + count + " " + bondToString(mol, bond));
        }
    }
    
    private void printCycles(IAtomContainer mol, ChainDecomposition chainDecomposition) {
        printChains("Cycle", mol, chainDecomposition.getCycleChains());
    }
    
    private void printPaths(IAtomContainer mol, ChainDecomposition chainDecomposition) {
        printChains("Path", mol, chainDecomposition.getPathChains());
    }
    
    private void printChains(String prefix, IAtomContainer mol, List<List<IBond>> chains) {
        int count = 1;
        for (List<IBond> cycle : chains) {
            System.out.print(prefix + " " + count + " ");
            for (IBond bond : cycle) {
                System.out.print(bondToString(mol, bond) + ",");
            }
            System.out.println();
            count++;
        }
    }
    
    private void printAll(IAtomContainer mol, ChainDecomposition chainDecomposition) {
        printBridges(mol, chainDecomposition);
        printCycles(mol, chainDecomposition);
        printPaths(mol, chainDecomposition);
    }
    
    private String bondToString(IAtomContainer mol, IBond bond) {
        return mol.getAtomNumber(bond.getAtom(0)) + ":" +  mol.getAtomNumber(bond.getAtom(1));
    }
    
    private IAtomContainer get(String acp) {
        return AtomContainerPrinter.fromString(acp, builder);
    }
    
    @Test
    public void spira() {
        IAtomContainer mol = get("C0C1C2C3C4C5C6 0:1(1),0:3(1),1:2(1),2:3(1),3:4(1),3:6(1),4:5(1),5:6(1)");
        ChainDecomposition chainDecomposition = new ChainDecomposition(mol);
        assertEquals(0, chainDecomposition.getBridges().size());
        printCycles(mol, chainDecomposition);
    }
    
    @Test
    public void bridgedRings() {
        IAtomContainer mol = get("C0C1C2C3C4C5C6 0:1(1),0:2(1),1:2(1),1:3(1),3:4(1),3:5(1),4:6(1),5:6(1)");
        ChainDecomposition chainDecomposition = new ChainDecomposition(mol);
        assertEquals(1, chainDecomposition.getBridges().size());
        printCycles(mol, chainDecomposition);
        printBridges(mol, chainDecomposition);
    }
    
    @Test
    public void fourLine() {
        IAtomContainer mol = get("C0C1C2C3 0:1(1),1:2(1),2:3(1)");
        ChainDecomposition chainDecomposition = new ChainDecomposition(mol);
        printBridges(mol, chainDecomposition);
        printPaths(mol, chainDecomposition);
    }
    
    @Test
    public void wonkyBowtie() {
        IAtomContainer mol = get("C0C1C2C3C4C5C6 0:1(1),0:2(1),1:3(1),2:4(1),3:5(1),0:4(1),1:5(1),5:6(1)");
        ChainDecomposition chainDecomposition = new ChainDecomposition(mol);
        printBridges(mol, chainDecomposition);
        printCycles(mol, chainDecomposition);
        printPaths(mol, chainDecomposition);
    }
    

    @Test
    public void bridgedArmedSquare() {
        IAtomContainer mol = get("C0C1C2C3C4C5C6 0:1(1),0:2(1),0:3(1),1:4(1),4:5(1),2:4(1),0:5(1),3:6(1)");
        ChainDecomposition chainDecomposition = new ChainDecomposition(mol);
        printAll(mol, chainDecomposition);
    }

}
