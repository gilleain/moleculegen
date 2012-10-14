package test.generate.filter;

import generate.ListerMethod;
import test.generate.BaseTest;

public class FilterTest extends BaseTest {
    
    public static final ListerMethod METHOD = ListerMethod.FILTER;
    
    public int countNFromAtom(String formula) {
        return countNFromAtom(formula, METHOD);
    }

}
