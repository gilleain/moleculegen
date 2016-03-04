package augment.bond;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.FastChemObjectBuilder;

import augment.constraints.ElementConstraints;
import io.AtomContainerPrinter;

public class TestBondCanonicalChecker {
    
    private IChemObjectBuilder builder =  FastChemObjectBuilder.getInstance();   // changed SLewis for control
    
    private BondCanonicalChecker checker = new BondCanonicalChecker();
    
    private boolean test(String acpString, String elementSymbol, int... params) {
        return test(acpString, new BondExtension(
                new IndexPair(params[0], params[1], params[2]), elementSymbol));
    }
    
    private boolean test(String acpString, BondExtension bondExtension) {
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        BondAugmentation augmentation = new BondAugmentation(ac, bondExtension, new ElementConstraints("C"));
        return checker.isCanonical(augmentation);
    }
    
    @Test
    public void testAlkaneA() {
        assertFalse(test("C0N1O2C3 0:1(1),1:2(1)", "C", 2, 3, 1));
    }
    
    @Test
    public void testBowtie() {
        assertFalse(test("C0C1C2C3C4C5 0:1(1),0:2(1),1:3(1),2:4(1),3:5(1),0:4(1),1:5(1)", "C", 0, 6, 1));
    }
    
    @Test
    public void testMultiFused() {
        assertFalse(test("C0C1C2C3C4C5C6 0:1(1),0:2(1),1:3(1),1:4(1),1:5(1),2:3(1),2:4(1),2:6(1)", "C", 5, 6, 1));
    }
    
    @Test
    public void testBridgedArmedSquare() {
        assertFalse(test("C0C1C2C3C4C5 0:1(1),0:2(1),0:3(1),1:4(1),4:5(1),2:4(1),0:5(1)", "C", 3, 6, 1));
    }
   
}
