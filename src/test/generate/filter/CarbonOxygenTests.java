package test.generate.filter;

import junit.framework.Assert;

import org.junit.Test;

public class CarbonOxygenTests extends FilterTest {
    
    @Test
    public void cH2OTest() {
        Assert.assertEquals(1, countNFromAtom("CH2O"));
    }
    
    @Test
    public void cH4OTest() {
        Assert.assertEquals(1, countNFromAtom("CH4O"));
    }
    
    @Test
    public void c2OTest() {
        Assert.assertEquals(1, countNFromAtom("C2O"));
    }
    
    @Test
    public void c2H2OTest() {
        Assert.assertEquals(3, countNFromAtom("C2H2O"));
    }
    
    @Test
    public void cO2Test() {
        Assert.assertEquals(1, countNFromAtom("CO2"));
    }
    
    @Test
    public void c2H4OTest() {
        Assert.assertEquals(3, countNFromAtom("C2H4O"));
    }

}
