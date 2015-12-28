package test.branch.bond;

import appbranch.augment.bond.BondGenerator;
import appbranch.handler.CountingHandler;

public class FormulaTest {
    
    public int countNFromAtom(String elementFormula) {
        CountingHandler handler = new CountingHandler(false);
        BondGenerator gen = new BondGenerator(elementFormula, handler);
        gen.run();
        return handler.getCount(); 
    }

}
