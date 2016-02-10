package app;

import java.io.File;

import org.junit.Test;

public class FileAugmentTests {
    
    /**
     * A random choice.
     */
    private String formula = "C5H10";
    
    private String testFolder = "testdata";
    
    public void augmentFileTest(String inForm, String outForm, String filename) throws Exception {
        ArgumentHandler argsH = new ArgumentHandler();
        argsH.setAugmentingFile(true);
        argsH.setFormula(formula);
        argsH.setInputStringFormat(inForm);
        argsH.setOutputStringFormat(outForm);
        argsH.setIsStdOut(true);
        argsH.setInputFilepath(new File(testFolder, filename).toString());
        AMG.run(argsH);
    }
    
    @Test
    public void augmentFile_SMI_To_SMI_Test() throws Exception {
       augmentFileTest("SMI", "SMI", "fours_smiles.txt");
    }
    
    @Test
    public void augmentFile_SMI_To_SIG_Test() throws Exception {
        augmentFileTest("SMI", "SIG", "fours_smiles.txt");
    }
    
    @Test
    public void augmentFile_SIG_To_SMI_Test() throws Exception {
        augmentFileTest("SIG", "SMI", "fours_sigs.txt");
    }
    
    @Test
    public void augmentFile_SIG_To_SIG_Test() throws Exception {
        augmentFileTest("SIG", "SIG", "fours_sigs.txt");
    }

}
