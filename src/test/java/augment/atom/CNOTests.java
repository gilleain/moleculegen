package augment.atom;

import org.junit.Assert;
import org.junit.Test;


public class CNOTests extends FormulaTest {

    @Test
    public void cH5NOTest() {
        Assert.assertEquals(3, countNFromAtom("CH5NO"));
    }
    
    @Test
    public void c3HNOTest() {
        int c3HNO = countNFromAtom("C3HNO");
        Assert.assertEquals(46, c3HNO);
    }
    
    @Test
    public void c2H7NOTest() {
        int c2H7NO = countNFromAtom("C2H7NO");
        Assert.assertEquals(8, c2H7NO);
    }
}
