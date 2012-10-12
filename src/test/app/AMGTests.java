package test.app;

import java.io.File;

import org.junit.Test;

import app.AMG;
import app.ArgumentHandler;

public class AMGTests {
    
    /**
     * A random choice.
     */
    private String formula = "C5H10";
    
    private String testFolder = "testdata";
    
    @Test
    public void augmentFile_SMI_To_SMI_Test() throws Exception {
        ArgumentHandler argsH = new ArgumentHandler();
        argsH.setAugmentingFile(true);
        argsH.setFormula(formula);
        argsH.setInputStringFormat("SMI");
        argsH.setOutputStringFormat("SMI");
        argsH.setIsStdOut(true);
        argsH.setInputFilepath(new File(testFolder, "fours_smiles.txt").toString());
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
