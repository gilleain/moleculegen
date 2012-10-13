package test.generate.symmetry;

import generate.AtomAugmentingGenerator.ListerMethod;
import test.generate.BaseTest;

public class SymmetryTest extends BaseTest {
    
    public static final ListerMethod METHOD = ListerMethod.SYMMETRIC;
    
    public int countNFromAtom(String formula) {
        return countNFromAtom(formula, METHOD);
    }

}
