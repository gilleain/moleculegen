package augment.bond;

import java.io.IOException;

import org.junit.Test;

import app.Generator;
import app.GeneratorFactory;
import app.TestFromFile;
import handler.Handler;

public class TestBondFromFile extends TestFromFile {
    
    @Test
    public void run() throws IOException {
        super.runTest(new GeneratorFactory() {

            @Override
            public Generator createForFormula(String elementFormula, Handler handler) {
                return new BondGenerator(elementFormula, handler);
            }
            
        });
    }


}
