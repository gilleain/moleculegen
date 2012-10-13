package test.generate.symmetry;

import generate.AtomAugmentingGenerator.ListerMethod;
import junit.framework.Assert;

import org.junit.Test;

import test.generate.BaseTest;

public class CarbonOnlyTests extends BaseTest {
    
    public static final ListerMethod METHOD = ListerMethod.SYMMETRIC;
    
    @Test
    public void c2Test() {
        Assert.assertEquals(0, countNFromSingleDoubleTriple("C2", METHOD));
    }
    
    @Test
    public void c3Test() {
        Assert.assertEquals(1, countNFromSingleDoubleTriple("C3", METHOD));
    }
    
    @Test
    public void c4Test() {
        Assert.assertEquals(3, countNFromSingleDoubleTriple("C4", METHOD));
    }
    
    @Test
    public void c5Test() {
        Assert.assertEquals(6, countNFromSingleDoubleTriple("C5", METHOD));
    }
    
    @Test
    public void c6Test() {
        Assert.assertEquals(19, countNFromSingleDoubleTriple("C6", METHOD));
    }

}
