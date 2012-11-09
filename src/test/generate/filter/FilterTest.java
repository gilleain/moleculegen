package test.generate.filter;

import generate.LabellerMethod;
import generate.ListerMethod;
import generate.ValidatorMethod;
import test.generate.BaseTest;

public class FilterTest extends BaseTest {
    
    public static final ListerMethod LISTER_METHOD = ListerMethod.FILTER;
    
    public static final LabellerMethod LABELLER_METHOD = LabellerMethod.SIGNATURE;
    
    public static final ValidatorMethod VALIDATOR_METHOD = ValidatorMethod.REFINER;
    
    public int countNFromAtom(String formula) {
        return countNFromAtom(formula, LISTER_METHOD, LABELLER_METHOD, VALIDATOR_METHOD);
    }

}
