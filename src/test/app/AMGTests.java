package test.app;

import org.junit.Test;

import app.AMG;
import app.ArgumentHandler;

public class AMGTests {
    
    /**
     * A random choice.
     */
    private String formula = "C5H10";
    
    @Test
    public void augmentFileTest() throws Exception {
        ArgumentHandler argsH = new ArgumentHandler();
        argsH.setAugmentingFile(true);
        argsH.setFormula(formula);
        AMG.run(argsH);
    }
    
    @Test
    public void startingFromScratchTest() throws Exception {
        ArgumentHandler argsH = new ArgumentHandler();
        argsH.setStartingFromScratch(true);
        argsH.setFormula(formula);
        AMG.run(argsH);
    }


}
