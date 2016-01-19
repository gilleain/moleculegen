package appbranch.constrained;

import java.io.IOException;

import org.junit.Test;

import appbranch.Generator;
import appbranch.GeneratorFactory;
import appbranch.handler.Handler;
import test.branch.TestFromFile;

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
