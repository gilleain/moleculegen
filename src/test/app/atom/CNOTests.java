package test.app.atom;

import org.junit.Test;

import junit.framework.Assert;

public class CNOTests extends FormulaTest {

    @Test
    public void cH5NOTest() {
        Assert.assertEquals(3, countNFromAtom("CH5NO"));
    }
    
    @Test
    public void c2H7NOTest() {
        Assert.assertEquals(8, countNFromAtom("C2H7NO"));
    }
}
