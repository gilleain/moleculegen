package test.generate.symmetry;

import generate.AtomAugmentingGenerator.ListerMethod;
import junit.framework.Assert;

import org.junit.Test;

import test.generate.BaseTest;

public class SubAlkyneTests extends BaseTest {
    
    public static final ListerMethod METHOD = ListerMethod.SYMMETRIC;
    
    @Test
    public void c3H2Test() {
        Assert.assertEquals(2, countNFromAtom("C3H2", METHOD));
    }
    
    @Test
    public void c4H2Test() {
        Assert.assertEquals(7, countNFromAtom("C4H2", METHOD));
    }
    
    @Test
    public void c4H4Test() {
        Assert.assertEquals(11, countNFromAtom("C4H4", METHOD));
    }
    
    @Test
    public void c5H2Test() {
        Assert.assertEquals(21, countNFromAtom("C5H2", METHOD));
    }
    
    @Test
    public void c5H4Test() {
        Assert.assertEquals(40, countNFromAtom("C5H4", METHOD));
    }
    
    @Test
    public void c5H6Test() {
        Assert.assertEquals(40, countNFromAtom("C5H6", METHOD));
    }
    
    @Test
    public void c6H2Test() {
        Assert.assertEquals(85, countNFromAtom("C6H2", METHOD));
    }
    
    @Test
    public void c6H4Test() {
        Assert.assertEquals(185, countNFromAtom("C6H4", METHOD));
    }
    
    @Test
    public void c6H6Test() {
        Assert.assertEquals(217, countNFromAtom("C6H6", METHOD));
    }
    
    @Test
    public void c6H8Test() {
        Assert.assertEquals(159, countNFromAtom("C6H8", METHOD));
    }

}
