package test.generate;

import generate.AugmentationMethod;

import org.junit.Test;

public class BondAugmentingGeneratorTest extends BaseTest {
    
    @Test
    public void countC4H6FromSDT() {
        int count = countNFromSingleDoubleTriple("C4H6", null, null, AugmentationMethod.BOND);
        System.out.println(count);
    }
    
    @Test
    public void countC5H10FromSDT() {
        int count = countNFromSingleDoubleTriple("C5H10", null, null, AugmentationMethod.BOND);
        System.out.println(count);
    }

}
