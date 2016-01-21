package test.app.atom;

import java.io.IOException;

import org.junit.Test;

import app.Generator;
import app.GeneratorFactory;
import app.augment.atom.AtomGenerator;
import app.handler.Handler;
import test.app.TestFromFile;

public class TestAtomFromFile extends TestFromFile {
    
    @Test
    public void run() throws IOException {
        super.runTest(new GeneratorFactory() {

            @Override
            public Generator createForFormula(String elementFormula, Handler handler) {
                return new AtomGenerator(elementFormula, handler);
            }
            
        });
    }

}
