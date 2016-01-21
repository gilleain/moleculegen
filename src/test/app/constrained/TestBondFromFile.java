package test.app.constrained;

import java.io.IOException;

import org.junit.Test;

import app.Generator;
import app.GeneratorFactory;
import augment.constrained.BondGenerator;
import handler.Handler;
import test.app.TestFromFile;

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
