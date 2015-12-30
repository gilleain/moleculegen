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
    
    @Test
    public void testA() {
        IAtomContainer ac = AtomContainerPrinter.fromString("C0C1C2C3 0:1(1),0:2(1),0:3(1)", builder);
        BondExtension bondExtension = new BondExtension(new IndexPair(1, 2, 1), new ElementPair("C", "C"));
        Augmentation<IAtomContainer, BondExtension> augmentation = new BondAugmentation(ac, bondExtension);
        boolean canon = checker.isCanonical(augmentation);
        assertTrue(canon);
    }
    
    @Test
    public void testB() {
        IAtomContainer ac = AtomContainerPrinter.fromString("C0C1C2C3 0:1(1),0:2(1),1:2(1)", builder);
        BondExtension bondExtension = new BondExtension(new IndexPair(0, 3, 1), new ElementPair("C", "C"));
        Augmentation<IAtomContainer, BondExtension> augmentation = new BondAugmentation(ac, bondExtension);
        boolean canon = checker.isCanonical(augmentation);
        assertFalse(canon);
    }

}
