package test.validate;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import test.AtomContainerPrinter;
import validate.HCountValidator;

public class HCountValidatorTest {
    
    private static final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    public void setHydrogens(IAtomContainer ac, int... hcounts) {
        for (int i = 0; i < hcounts.length; i++) {
            ac.getAtom(i).setImplicitHydrogenCount(hcounts[i]);
        }
    }

    @Test
    public void bugTest() {
        String acp = "C0C1C2O3O4O5 0:1(1),0:3(1),1:3(1),0:4(1),2:5(1),4:5(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acp, builder);
        setHydrogens(ac, 1, 2, 3, 0, 0, 0);
        HCountValidator validator = new HCountValidator();
        validator.setHCount(6);
        Assert.assertTrue(validator.isValidMol(ac, 6));
    }
}
