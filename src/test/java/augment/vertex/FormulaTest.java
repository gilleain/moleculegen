package augment.vertex;

import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import handler.Handler;
import handler.graph.MoleculeAdaptor;
import handler.molecule.DuplicateCountingHandler;
import handler.molecule.HBondCheckingHandler;
import model.Graph;

public class FormulaTest {
    
    public int countNFromAtom(String elementFormula) {
        return countNFromAtom(elementFormula, false);
    }
    
    public int countNFromAtom(String elementFormula, boolean printDupMap) {
        DuplicateCountingHandler moleculeHandler = new DuplicateCountingHandler();
        HBondCheckingHandler hBondCheckingHandler = 
                new HBondCheckingHandler(elementFormula, moleculeHandler);
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        Handler<Graph> moleculeAdaptor = new MoleculeAdaptor(builder, hBondCheckingHandler);
        VertexGenerator gen = new VertexGenerator(elementFormula, moleculeAdaptor);
        gen.run();
        Map<String, List<IAtomContainer>> map = moleculeHandler.getDupMap();
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
