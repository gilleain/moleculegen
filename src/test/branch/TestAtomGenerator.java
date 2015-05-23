package test.branch;

import io.AtomContainerPrinter;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import branch.AtomGenerator;
import branch.CountingHandler;
import branch.DuplicateHandler;
import branch.Handler;
import branch.PrintStreamHandler;

public class TestAtomGenerator {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    private void run(String elementSymbols, String fromString, Handler handler) {
        AtomGenerator gen = new AtomGenerator(elementSymbols, handler, elementSymbols.length());
        gen.run(AtomContainerPrinter.fromString(fromString, builder));
    }
    
    private void printFrom(String elementSymbols, String fromString) {
        run(elementSymbols, fromString, new PrintStreamHandler(System.out));
    }
    
    private int countFrom(String elementSymbols, String fromString) {
        CountingHandler handler = new CountingHandler();
        AtomGenerator gen = new AtomGenerator(elementSymbols, handler, elementSymbols.length());
        gen.run(AtomContainerPrinter.fromString(fromString, builder));
        return handler.getCount();
    }
    
    @Test
    public void testFromCCSingle() {
        printFrom("CCCC", "C0C1 0:1(1)");
    }
    
    @Test
    public void testFromCCDouble() {
        printFrom("CCCC", "C0C1 0:1(2)");
    }
    
    @Test
    public void testToFours() {
        int count  = countFrom("CCCC", "C0C1 0:1(1)");
            count += countFrom("CCCC", "C0C1 0:1(2)");
            count += countFrom("CCCC", "C0C1 0:1(3)");
        System.out.println(count);
    }
    
    @Test
    public void testDups() {
        DuplicateHandler handler = new DuplicateHandler();
        run("CCCC", "C0C1 0:1(1)", handler);
        run("CCCC", "C0C1 0:1(2)", handler);
        run("CCCC", "C0C1 0:1(3)", handler);
        Map<String, List<IAtomContainer>> map = handler.getDupMap();
        int count = 0;
        for (String key : map.keySet()) {
            List<IAtomContainer> dups = map.get(key);
            if (dups.size() == 1) {
                System.out.println(count + "\t1" + AtomContainerPrinter.toString(dups.get(0)));
            } else {
                System.out.println(count + "\t" + dups.size());
            }
            count++;
        }
    }

}
