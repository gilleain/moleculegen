package test.branch.atom;

import java.io.IOException;

import org.junit.Test;

import appbranch.Generator;
import appbranch.GeneratorFactory;
import appbranch.augment.atom.AtomGenerator;
import appbranch.handler.Handler;
import test.branch.TestFromFile;

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
