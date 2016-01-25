package augment.atomcontrained;

import java.io.IOException;

import org.junit.Test;

import app.GeneratorFactory;
import app.TestFromFile;
import augment.AugmentingGenerator;
import augment.atomconstrained.AtomGenerator;
import handler.Handler;

public class TestAtomFromFile extends TestFromFile {
    
    @Test
    public void run() throws IOException {
        super.runTest(new GeneratorFactory() {

            @Override
            public AugmentingGenerator createForFormula(String elementFormula, Handler handler) {
                return new AtomGenerator(elementFormula, handler);
            }
            
        });
    }

}
