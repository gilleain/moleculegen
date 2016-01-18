package test.branch.constrained;

import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;

import appbranch.constrained.BondGenerator;
import appbranch.handler.DuplicateCountingHandler;

public class FormulaTest {
    
    public int countNFromAtom(String elementFormula) {
//        CountingHandler handler = new CountingHandler(false);
        DuplicateCountingHandler handler = new DuplicateCountingHandler();
        BondGenerator gen = new BondGenerator(elementFormula, handler);
        gen.run();
        Map<String, List<IAtomContainer>> map = handler.getDupMap();
        int i = 0;
        for (String s : map.keySet()) {
            System.out.println(i++ + " " + s + " = " + io.AtomContainerPrinter.toString(map.get(s).get(0)));
        }
        return map.keySet().size(); 
    }

}
