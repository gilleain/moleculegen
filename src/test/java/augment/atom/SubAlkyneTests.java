package augment.atom;

import junit.framework.Assert;

import org.junit.Test;

public class SubAlkyneTests extends FormulaTest {
    
    @Test
    public void c3H2Test() {
        Assert.assertEquals(2, countNFromAtom("C3H2"));
    }
    
    @Test
    public void c4H2Test() {
        Assert.assertEquals(7, countNFromAtom("C4H2"));
    }
    
    @Test
    public void c4H4Test() {
        Assert.assertEquals(11, countNFromAtom("C4H4"));
    }
    
    @Test
    public void c5H2Test() {
        Assert.assertEquals(21, countNFromAtom("C5H2"));
    }
    
    @Test
    public void c5H4Test() {
        Assert.assertEquals(40, countNFromAtom("C5H4"));
    }
    
    @Test
    public void c5H6Test() {
        Assert.assertEquals(40, countNFromAtom("C5H6"));
    }
    
    @Test
    public void c6H2Test() {
        Assert.assertEquals(85, countNFromAtom("C6H2"));
    }
    
    @Test
    public void c6H4Test() {
        Assert.assertEquals(185, countNFromAtom("C6H4"));
    }
    
    @Test
    public void c6H6Test() {
        Assert.assertEquals(217, countNFromAtom("C6H6"));
    }
    
    @Test
    public void c6H8Test() {
        Assert.assertEquals(159, countNFromAtom("C6H8"));
    }
    
    @Test
    public void c7H2Test() {
        Assert.assertEquals(356, countNFromAtom("C7H2"));
    }
    
    @Test
    public void c7H8Test() {
        Assert.assertEquals(1031, countNFromAtom("C7H8"));
    }
}
