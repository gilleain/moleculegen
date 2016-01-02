package test.branch.bond;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openscience.cdk.group.AtomContainerPrinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import appbranch.augment.Augmentation;
import appbranch.augment.bond.BondAugmentation;
import appbranch.augment.bond.BondCanonicalChecker;
import appbranch.augment.bond.BondExtension;
import appbranch.augment.bond.ElementPair;
import appbranch.augment.bond.IndexPair;

public class TestBondCanonicalChecker {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    private BondCanonicalChecker checker = new BondCanonicalChecker();
    
    private boolean test(String acpString, int... params) {
        return test(acpString, new BondExtension(
                new IndexPair(params[0], params[1], params[2]), new ElementPair("C", "C")));
    }
    
    private boolean test(String acpString, BondExtension bondExtension) {
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        Augmentation<IAtomContainer, BondExtension> augmentation = new BondAugmentation(ac, bondExtension);
        return checker.isCanonical(augmentation);
    }
    
    @Test
    public void testAlkaneA() {
        assertTrue(test("C0C1C2C3 0:1(1),0:2(1),0:3(1)", 1, 2, 1));
    }
    
    @Test
    public void testAlkaneB() {
        assertFalse(test("C0C1C2C3 0:1(1),0:2(1),1:2(1)", 0, 3, 1));
    }
    
    @Test
    public void testAlkeneA() {
        assertTrue(test("C0C1C2 0:1(2),0:2(1)", 0, 3, 1));
    }
    
    @Test
    public void testAlkeneB() {
        assertFalse(test("C0C1C2 0:1(1),0:2(2)", 0, 3, 1));
    }
    
    @Test
    public void testAlkeneC() {
        assertTrue(test("C0C1C2 0:1(2)", 0, 2, 1));
    }

    @Test
    public void testAlkeneD() {
        assertFalse(test("C0C1 0:1(1)", 0, 2, 2));
    }
    
    @Test
    public void testAlkyneA() {
        assertTrue(test("C0C1C2C3 0:1(1),0:2(1),0:3(1)", 1, 2, 2));
    }
    
    @Test
    public void testAlkyneB() {
        assertFalse(test("C0C1C2C3 0:1(2),0:2(1),1:2(1)", 2, 3, 1));
    }


}
