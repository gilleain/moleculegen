package test.branch;

import appbranch.AtomGenerator;
import appbranch.handler.CountingHandler;

public class FormulaTest {
    
    public int countNFromAtom(String elementFormula) {
        CountingHandler handler = new CountingHandler();
        AtomGenerator gen = new AtomGenerator(elementFormula, handler);
        gen.run();
        return handler.getCount(); 
    }

}
