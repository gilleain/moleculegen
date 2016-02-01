package app;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import augment.AugmentingGenerator;
import augment.vertex.VertexGenerator;
import handler.Handler;
import handler.graph.MoleculeAdaptor;

@SuppressWarnings("rawtypes")
public class TestAugmentingGeneratorFactory {
    
    @Test
    public void testMakeCountingAtomGen() throws IOException {
        ArgumentHandler argsH = new ArgumentHandler();
        argsH.setAugmentationMethod(AugmentationMethod.ATOM);
        argsH.setFormula("C3H8");
        argsH.setComparingToFile(false);
        argsH.setOutputStringFormat("");
        
        AugmentingGenerator generator = AugmentingGeneratorFactory.build(argsH);
        assertEquals(generator.getClass(), VertexGenerator.class);
        Handler handler = generator.getHandler();
        assertEquals(handler.getClass(), MoleculeAdaptor.class);
    }

}
