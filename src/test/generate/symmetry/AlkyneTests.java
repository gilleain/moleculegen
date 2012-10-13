package test.generate.symmetry;

import generate.AtomAugmentingGenerator.ListerMethod;
import junit.framework.Assert;

import org.junit.Test;

import test.generate.BaseTest;

public class AlkyneTests extends BaseTest {
    
    public static final ListerMethod METHOD = ListerMethod.SYMMETRIC;
    
    @Test
    public void c2H2Test() {
        Assert.assertEquals(1, countNFromAtom("C2H2", METHOD));
    }
    
    @Test
    public void c3H4Test() {
        Assert.assertEquals(3, countNFromAtom("C3H4", METHOD));
    }
    
    @Test
    public void c4H6Test() {
        Assert.assertEquals(9, countNFromAtom("C4H6", METHOD));
    }
    
    @Test
    public void c5H8Test() {
        Assert.assertEquals(26, countNFromAtom("C5H8", METHOD));
    }
    
    @Test
    public void c6H10Test() {
        Assert.assertEquals(77, countNFromAtom("C6H10", METHOD));
    }

}
