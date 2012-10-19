package test.generate.filter;

import generate.ListerMethod;
import generate.ValidatorMethod;
import test.generate.BaseTest;

public class FilterTest extends BaseTest {
    
    public static final ListerMethod LISTER_METHOD = ListerMethod.FILTER;
    
    public static final ValidatorMethod VALIDATOR_METHOD = ValidatorMethod.SIGNATURE;
    
    public int countNFromAtom(String formula) {
        return countNFromAtom(formula, LISTER_METHOD, VALIDATOR_METHOD);
    }

}
