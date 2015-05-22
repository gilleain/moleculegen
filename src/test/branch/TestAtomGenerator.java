package test.branch;

import io.AtomContainerPrinter;

import org.junit.Test;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import branch.AtomGenerator;
import branch.Handler;
import branch.PrintStreamHandler;

public class TestAtomGenerator {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    private void testFrom(String elementSymbols, String fromString) {
        Handler handler = new PrintStreamHandler(System.out);
        AtomGenerator gen = new AtomGenerator(elementSymbols, handler, elementSymbols.length());
        gen.run(AtomContainerPrinter.fromString(fromString, builder));
    }
    
    @Test
    public void testFromCCSingle() {
        testFrom("CCCC", "C0C1 0:1(1)");
    }
    
    @Test
    public void testFromCCDouble() {
        testFrom("CCCC", "C0C1 0:1(2)");
    }

}
