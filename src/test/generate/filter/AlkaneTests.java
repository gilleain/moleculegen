package test.generate.filter;

import generate.AtomAugmentingGenerator.ListerMethod;
import junit.framework.Assert;

import org.junit.Test;

import test.generate.BaseTest;

public class AlkaneTests extends BaseTest {
    
    public static final ListerMethod METHOD = ListerMethod.FILTER;
    
    @Test
    public void c2H6Test() {
        Assert.assertEquals(1, countNFromAtom("C2H6", METHOD));
    }
    
    @Test
    public void c3H8Test() {
        Assert.assertEquals(1, countNFromAtom("C3H8", METHOD));
    }
    
    @Test
    public void c4H10Test() {
        Assert.assertEquals(2, countNFromAtom("C4H10", METHOD));
    }
    
    @Test
    public void c5H12Test() {
        Assert.assertEquals(3, countNFromAtom("C5H12", METHOD));
    }
    
    @Test
    public void c6H14Test() {
        Assert.assertEquals(5, countNFromAtom("C6H14", METHOD));
    }

}
