package augment.chem;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import app.FormulaParser;
import io.AtomContainerPrinter;
import junit.framework.Assert;

public class HCountValidatorTest {
    
    private static final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    public void setHydrogens(IAtomContainer ac, int... hcounts) {
        for (int i = 0; i < hcounts.length; i++) {
            ac.getAtom(i).setImplicitHydrogenCount(hcounts[i]);
        }
    }

    @Test
    public void bugTest() {
        int size = 6;
        String formula = "C3H6O3";
        String acp = "C0C1C2O3O4O5 0:1(1),0:3(1),1:3(1),0:4(1),2:5(1),4:5(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acp, builder);
        setHydrogens(ac, 1, 2, 3, 0, 0, 0);
        HCountValidator validator = new HCountValidator(new FormulaParser(formula));
        Assert.assertTrue(validator.isValidMol(ac, size));
    }
    
    @Test
    public void dimethylNitrogenTest() {
        int size = 3;
        int hCount = 7;
        String formula = "C2NH7";
        String acp = "C0C1N2 0:2(1),1:2(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acp, builder);
        setHydrogens(ac, 3, 3, 1);
        HCountValidator validator = new HCountValidator(new FormulaParser(formula));
        Assert.assertTrue(validator.isValidMol(ac, size));
    }
}
