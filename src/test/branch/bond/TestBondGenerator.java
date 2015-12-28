package test.branch.bond;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import appbranch.augment.bond.BondGenerator;
import appbranch.handler.CountingHandler;
import appbranch.handler.Handler;
import appbranch.handler.PrintStreamHandler;
import io.AtomContainerPrinter;

public class TestBondGenerator {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    private void run(String elementFormula, String fromString, Handler handler) {
        BondGenerator gen = new BondGenerator(elementFormula, handler);
        gen.run(AtomContainerPrinter.fromString(fromString, builder));
    }
    
    private void print(String elementFormula) {
        BondGenerator gen = new BondGenerator(elementFormula, new PrintStreamHandler(System.out));
        gen.run();
    }
    
    private int count(String elementFormula) {
        CountingHandler handler = new CountingHandler(false);
        BondGenerator gen = new BondGenerator(elementFormula, handler);
        gen.run();
        return handler.getCount();
    }
    
    @Test
    public void testC2H4() {
//        print("C2H4");
        assertEquals(1, count("C2H4"));
    }
    
    @Test
    public void testC3H6() {
//        print("C3H6");
        assertEquals(2, count("C3H6"));
    }
    
    @Test
    public void testC4H8() {
//        print("C4H8");
        assertEquals(5, count("C4H8"));
    }
    
    @Test
    public void testC5H10() {
        print("C5H10");
//        assertEquals(10, count("C5H10"));
    }
    
}
