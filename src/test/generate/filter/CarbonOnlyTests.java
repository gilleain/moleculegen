package test.generate.filter;

import junit.framework.Assert;

import org.junit.Test;

public class CarbonOnlyTests extends FilterTest {
    
    @Test
    public void c2Test() {
        Assert.assertEquals(0, countNFromAtom("C2"));
    }
    
    @Test
    public void c3Test() {
        Assert.assertEquals(1, countNFromAtom("C3"));
    }
    
    @Test
    public void c4Test() {
        Assert.assertEquals(3, countNFromAtom("C4"));
    }
    
    @Test
    public void c5Test() {
        Assert.assertEquals(6, countNFromAtom("C5"));
    }
    
    @Test
    public void c6Test() {
        Assert.assertEquals(19, countNFromAtom("C6"));
    }

}
