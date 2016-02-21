package augment.vertex;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import handler.Handler;
import handler.graph.MoleculeAdaptor;
import handler.molecule.HBondCheckingHandler;
import handler.molecule.PrintStreamHandler;
import model.Graph;

/**
 * Grow from a seed molecule.
 * 
 * @author maclean
 *
 */
public class SeedTest {
    
    private void grow(String elementFormula, String seed) {
        Handler<IAtomContainer> moleculeHandler = new PrintStreamHandler(System.out, false);
        HBondCheckingHandler hBondCheckingHandler = 
                new HBondCheckingHandler(elementFormula, moleculeHandler);
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        Handler<Graph> moleculeAdaptor = new MoleculeAdaptor(builder, hBondCheckingHandler);
        VertexGenerator gen = new VertexGenerator(elementFormula, moleculeAdaptor);
        gen.run(new Graph(seed));
        gen.finish();
    }
    
    @Test
    public void ringOfSix() {
        grow("C9H18", "C0C1C2C3C4C5 0:1(1),0:5(1),1:2(1),2:3(1),3:4(1),4:5(1)");
    }
    
    @Test
    public void ringOfFour() {
        grow("C6H10", "C0C1C2C3 0:1(1),0:3(1),1:2(1),2:3(1)");
    }
    
    @Test
    public void coco() {
        grow("C2H4O2", "C0C1O2 0:1(1),0:2(1)");
    }

}
