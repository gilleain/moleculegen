package augment.atomcontrained;

import org.junit.Test;

import junit.framework.Assert;

public class AlkyneTests extends FormulaTest {
    
    @Test
    public void c2H2Test() {
        Assert.assertEquals(1, countNFromAtom("C2H2"));
    }
    
    @Test
    public void c3H4Test() {
        Assert.assertEquals(3, countNFromAtom("C3H4"));
    }
    
    @Test
    public void c4H6Test() {
        Assert.assertEquals(9, countNFromAtom("C4H6"));
    }
    
    @Test
    public void c5H8Test() {
        Assert.assertEquals(26, countNFromAtom("C5H8"));
    }
    
    @Test
    public void c6H10Test() {
        Assert.assertEquals(77, countNFromAtom("C6H10"));
    }
    
    @Test
    public void c7H12Test() {
        Assert.assertEquals(222, countNFromAtom("C7H12"));
    }

}
