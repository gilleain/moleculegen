package test.generate.filter;

import generate.AtomAugmentingGenerator.ListerMethod;
import junit.framework.Assert;

import org.junit.Test;

import test.generate.BaseTest;

public class AlkeneTests extends BaseTest {
    
    public static final ListerMethod METHOD = ListerMethod.FILTER;
    
    @Test
    public void c2H4Test() {
        Assert.assertEquals(1, countNFromSingleDoubleTriple("C2H4", METHOD));
    }
    
    @Test
    public void c3H6Test() {
        Assert.assertEquals(2, countNFromSingleDoubleTriple("C3H6", METHOD));
    }
    
    @Test
    public void c4H8Test() {
        Assert.assertEquals(5, countNFromSingleDoubleTriple("C4H8", METHOD));
    }
    
    @Test
    public void c5H10Test() {
        Assert.assertEquals(10, countNFromSingleDoubleTriple("C5H10", METHOD));
    }
    
    @Test
    public void c6H12Test() {
        Assert.assertEquals(25, countNFromSingleDoubleTriple("C6H12", METHOD));
    }

}
