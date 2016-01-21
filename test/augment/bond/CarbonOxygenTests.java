package augment.bond;

import org.junit.Test;

import junit.framework.Assert;

public class CarbonOxygenTests extends FormulaTest {
    
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
    public void c2O2Test() {
        Assert.assertEquals(3, countNFromAtom("C2O2"));
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
    
    @Test
    public void c2H6OTest() {
        Assert.assertEquals(2, countNFromAtom("C2H6O"));
    }
    
    @Test
    public void c2H4O2Test() {
        Assert.assertEquals(10, countNFromAtom("C2H4O2"));
    }
  
}
