package validate;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import io.AtomContainerPrinter;

public class BondCanonicalValidatorTest {
    
    public static final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    @Test
    public void triangleTest() {
//        String acpAString = "C0C1C2C3 0:1(2),0:2(1),0:3(1),1:2(1)";
        String acpAString = "C0C1C2C3 0:1(1),0:2(1),0:3(1),1:2(2)";
        IAtomContainer molA = AtomContainerPrinter.fromString(acpAString, builder);
        
//        String acpBString = "C0C1C2C3 0:1(2),0:2(1),1:2(1),0:3(1)";
        String acpBString = "C0C1C2C3 0:1(2),0:2(1),1:2(1),2:3(1)";
        IAtomContainer molB = AtomContainerPrinter.fromString(acpBString, builder);
        
        BondCanonicalValidator validator = new BondCanonicalValidator();
        boolean isA = validator.isCanonical(molA);
        boolean isB = validator.isCanonical(molB);
        System.out.println(isA + "\t" + isB);
    }

}
