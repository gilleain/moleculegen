package test.generate.symmetry;

import generate.AtomAugmentingGenerator.ListerMethod;
import junit.framework.Assert;

import org.junit.Test;

import test.generate.BaseTest;

public class CarbonOnlyTests extends BaseTest {
    
    public static final ListerMethod METHOD = ListerMethod.SYMMETRIC;
    
    @Test
    public void c2Test() {
        Assert.assertEquals(0, countNFromAtom("C2", METHOD));
    }
    
    @Test
    public void c3Test() {
        Assert.assertEquals(1, countNFromAtom("C3", METHOD));
    }
    
    @Test
    public void c4Test() {
        Assert.assertEquals(3, countNFromAtom("C4", METHOD));
    }
    
    @Test
    public void c5Test() {
        Assert.assertEquals(6, countNFromAtom("C5", METHOD));
    }
    
    @Test
    public void c6Test() {
        Assert.assertEquals(19, countNFromAtom("C6", METHOD));
    }

}
