package test.branch.atom;

import appbranch.augment.atom.AtomGenerator;
import appbranch.handler.CountingHandler;

public class FormulaTest {
    
    public int countNFromAtom(String elementFormula) {
        CountingHandler handler = new CountingHandler(false);
        AtomGenerator gen = new AtomGenerator(elementFormula, handler);
        gen.run();
        return handler.getCount(); 
    }

}
