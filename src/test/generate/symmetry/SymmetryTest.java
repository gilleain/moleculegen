package test.generate.symmetry;

import generate.ListerMethod;
import generate.ValidatorMethod;
import test.generate.BaseTest;

public class SymmetryTest extends BaseTest {

    public static final ListerMethod LISTER_METHOD = ListerMethod.SYMMETRIC;

    public static final ValidatorMethod VALIDATOR_METHOD = ValidatorMethod.REFINER;

    public int countNFromAtom(String formula) {
        return countNFromAtom(formula, LISTER_METHOD, VALIDATOR_METHOD);
    }
}
