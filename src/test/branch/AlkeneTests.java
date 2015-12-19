package test.branch;

import org.junit.Test;

import junit.framework.Assert;

public class AlkeneTests extends FormulaTest {
    
    @Test
    public void c2H4Test() {
        Assert.assertEquals(1, countNFromAtom("C2H4"));
    }
    
    @Test
    public void c3H6Test() {
        Assert.assertEquals(2, countNFromAtom("C3H6"));
    }
    
    @Test
    public void c4H8Test() {
        Assert.assertEquals(5, countNFromAtom("C4H8"));
    }
    
    @Test
    public void c5H10Test() {
        Assert.assertEquals(10, countNFromAtom("C5H10"));
    }
    
    @Test
    public void c6H12Test() {
        Assert.assertEquals(25, countNFromAtom("C6H12"));
    }
    
    @Test
    public void c9H16Test() {
        Assert.assertEquals(25, countNFromAtom("C9H16"));
    }

}
