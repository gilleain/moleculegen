package augment.atom;

import org.junit.Test;

import junit.framework.Assert;

public class AlkaneTests extends FormulaTest {
    
    
    @Test
    public void c2H6Test() {
        Assert.assertEquals(1, countNFromAtom("C2H6"));
    }
    
    @Test
    public void c3H8Test() {
        int c3H8 = countNFromAtom("C3H8");
        Assert.assertEquals(1, c3H8);
    }
    
    @Test
    public void c4H10Test() {
        int c4H10 = countNFromAtom("C4H10");
        Assert.assertEquals(2, c4H10);
    }
    
    @Test
    public void c5H12Test() {
        Assert.assertEquals(3, countNFromAtom("C5H12"));
    }
    
    @Test
    public void c6H14Test() {
        Assert.assertEquals(5, countNFromAtom("C6H14"));
    }

    @Test
    public void c7H16Test() {
        Assert.assertEquals(9, countNFromAtom("C7H16"));
    }

  //  @Test
    public void c12H26Test() {
        int c12H26 = countNFromAtom("C12H26");
        System.out.println(c12H26);
    }

    @Test
    public void c8H14Test() {
        int c8H14 = countNFromAtom("C8H14");
        Assert.assertEquals(654, c8H14);
    }

 //  @Test
    public void c10H18Test() {
        int c8H14 = countNFromAtom("C10H18");
        Assert.assertEquals(5572, c8H14);
    }

}