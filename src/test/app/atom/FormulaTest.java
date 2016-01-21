package test.app.atom;

import augment.atom.AtomGenerator;
import handler.CountingHandler;

public class FormulaTest {
    
    public int countNFromAtom(String elementFormula) {
        CountingHandler handler = new CountingHandler(false);
        AtomGenerator gen = new AtomGenerator(elementFormula, handler);
        gen.run();
        return handler.getCount(); 
    }

}
