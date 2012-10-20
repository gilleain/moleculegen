package test.generate;

import generate.AugmentMethod;

import org.junit.Test;

public class BondAugmentingGeneratorTest extends BaseTest {
    
    @Test
    public void countC4H6FromSDT() {
        int count = countNFromSingleDoubleTriple("C4H6", null, null, AugmentMethod.BOND);
        System.out.println(count);
    }

}
