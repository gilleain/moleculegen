package test.generate.symmetry;

import generate.AtomAugmentingGenerator.ListerMethod;
import junit.framework.Assert;

import org.junit.Test;

import test.generate.BaseTest;

public class AlkeneTests extends BaseTest {
    
    public static final ListerMethod METHOD = ListerMethod.SYMMETRIC;
    
    @Test
    public void c2H4Test() {
        Assert.assertEquals(1, countNFromAtom("C2H4", METHOD));
    }
    
    @Test
    public void c3H6Test() {
        Assert.assertEquals(2, countNFromAtom("C3H6", METHOD));
    }
    
    @Test
    public void c4H8Test() {
        Assert.assertEquals(5, countNFromAtom("C4H8", METHOD));
    }
    
    @Test
    public void c5H10Test() {
        Assert.assertEquals(10, countNFromAtom("C5H10", METHOD));
    }
    
    @Test
    public void c6H12Test() {
        Assert.assertEquals(25, countNFromAtom("C6H12", METHOD));
    }

}
