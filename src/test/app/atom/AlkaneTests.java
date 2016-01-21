package test.app.atom;

import org.junit.Test;

import junit.framework.Assert;

public class AlkaneTests extends FormulaTest {
    
    
    @Test
    public void c2H6Test() {
        Assert.assertEquals(1, countNFromAtom("C2H6"));
    }
    
    @Test
    public void c3H8Test() {
        Assert.assertEquals(1, countNFromAtom("C3H8"));
    }
    
    @Test
    public void c4H10Test() {
        Assert.assertEquals(2, countNFromAtom("C4H10"));
    }
    
    @Test
    public void c5H12Test() {
        Assert.assertEquals(3, countNFromAtom("C5H12"));
    }
    
    @Test
    public void c6H14Test() {
        Assert.assertEquals(5, countNFromAtom("C6H14"));
    }

}
