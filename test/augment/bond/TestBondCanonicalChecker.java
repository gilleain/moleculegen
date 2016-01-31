package augment.bond;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import augment.constraints.ElementConstraints;
import io.AtomContainerPrinter;

public class TestBondCanonicalChecker {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
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
        assertTrue(test("C0N1O2C3 0:1(1),1:2(1)", "C", 2, 3, 1));
    }
   
}
