package test.branch;

import org.junit.Test;

import appbranch.AMG;
import appbranch.ArgumentHandler;

public class FromScratchTests {
    
    /**
     * A random choice.
     */
    private String formula = "C5H10";
    
    public void fromScratchTest(String outputFormat) throws Exception {
        ArgumentHandler argsH = new ArgumentHandler();
        argsH.setStartingFromScratch(true);
        argsH.setOutputStringFormat(outputFormat);
        argsH.setFormula(formula);
        argsH.setIsStdOut(true);
        AMG.run(argsH);
    }
    
    @Test
    public void smilesFromScratchTest() throws Exception {
        fromScratchTest("SMI");
    }
    
    @Test
    public void signaturesFromScratchTest() throws Exception {
        fromScratchTest("SIG");
    }

    @Test
    public void sdfFromScratchTest() throws Exception {
        fromScratchTest("SDF");
    }

}
