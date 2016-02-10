package augment.atom;

import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;

import handler.molecule.DuplicateCountingHandler;

public class FormulaTest {
    
    public int countNFromAtom(String elementFormula) {
        return countNFromAtom(elementFormula, false);
    }
    
    public int countNFromAtom(String elementFormula, boolean printDupMap) {
        DuplicateCountingHandler handler = new DuplicateCountingHandler();
        AtomGenerator gen = new AtomGenerator(elementFormula, handler);
        gen.run();
        Map<String, List<IAtomContainer>> map = handler.getDupMap();
        if (printDupMap) { printDupMap(map); }
        int total = 0;
        for (String s : map.keySet()) {
            total += map.get(s).size();
        }
        return total; 
    }
    
    private void printDupMap(Map<String, List<IAtomContainer>> map) {
        int i = 0;
        for (String s : map.keySet()) {
            System.out.println(i++ + " " + s + " = " + printDups(map.get(s)));
        }
    }
    
    private String printDups(List<IAtomContainer> dups) {
        String s = "";
        for (IAtomContainer dup : dups) {
            s += io.AtomContainerPrinter.toString(dup) + " ";
        }
        return s;
    }

}
