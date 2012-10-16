package test.generate.symmetry;

import generate.ListerMethod;
import handler.DuplicateCountingHandler;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

import test.AtomContainerPrinter;
import test.generate.BaseTest;

public class DuplicateTests extends BaseTest {
    
    public void testForDuplicates(String formula) {
        DuplicateCountingHandler handler = new DuplicateCountingHandler();
        generateNFromAtom(formula, ListerMethod.SYMMETRIC, handler);
        Map<String, List<IAtomContainer>> dupMap = handler.getDupMap();
        for (String sig : dupMap.keySet()) {
            List<IAtomContainer> dups = dupMap.get(sig);
            for (IAtomContainer ac : dups) {
                AtomContainerPrinter.print(ac);
            }
            System.out.println("----------");
        }
    }
    
    @Test
    public void testC6H4() {
        testForDuplicates("C6H4");
    }
    
    @Test
    public void testC7H8() {
        testForDuplicates("C7H8");
    }
    
    @Test
    public void testC7H2() {
        testForDuplicates("C7H2");
    }

}
