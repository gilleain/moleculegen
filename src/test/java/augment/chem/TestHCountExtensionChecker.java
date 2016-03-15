package augment.chem;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import augment.constraints.ElementConstraints;

public class TestHCountExtensionChecker {
    
    private void test(int hCount, String partialString, String... elements) {
        List<String> elementList = new ArrayList<String>();
        for (String element : elements) { elementList.add(element); }
        IAtomContainer partial = 
                io.AtomContainerPrinter.fromString(
                        partialString, SilentChemObjectBuilder.getInstance());
        
        HCountExtensionChecker checker = new HCountExtensionChecker(hCount);
        assertTrue(checker.canExtend(partial, new ElementConstraints(elementList)));
    }
    
    @Test
    public void testCCBondToC3H8() {
        int hCount = 8;
        test(hCount, "C0C1 0:1(1)", "C");
    }
    
    @Test
    public void testCCBondToC3H6() {
        int hCount = 6;
        test(hCount, "C0C1 0:1(1)", "C");
    }

}
