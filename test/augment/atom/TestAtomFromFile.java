package augment.atom;

import java.io.IOException;

import org.junit.Test;

import app.GeneratorFactory;
import app.TestFromFile;
import augment.AugmentingGenerator;
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
