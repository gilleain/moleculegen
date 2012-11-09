package test.validate;

import io.AtomContainerPrinter;
import generate.AtomSymmetricChildLister;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;


import test.generate.BaseTest;
import validate.SignatureCanonicalValidator;

public class SignatureCanonicalValidatorTest extends BaseTest {
    
    public IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    public IAtomContainer make3Star() {
        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        atomContainer.addAtom(builder.newInstance(IAtom.class, ("C")));
        atomContainer.addAtom(builder.newInstance(IAtom.class, ("C")));
        atomContainer.addAtom(builder.newInstance(IAtom.class, ("C")));
        atomContainer.addAtom(builder.newInstance(IAtom.class, ("C")));
        atomContainer.addBond(0, 1, IBond.Order.SINGLE);
        atomContainer.addBond(0, 2, IBond.Order.SINGLE);
        atomContainer.addBond(0, 3, IBond.Order.SINGLE);
        return atomContainer;
    }
    
    @Test
    public void canonicalChecking() {
        IAtomContainer parent = make3Star();
        AtomContainerPrinter.print(parent);
        AtomSymmetricChildLister lister = new AtomSymmetricChildLister(elementSymbols("CCCCC"));
        SignatureCanonicalValidator validator = new SignatureCanonicalValidator();
        int len = parent.getAtomCount();
        for (IAtomContainer child : lister.listChildren(parent, len)) {
            boolean isCanonical = validator.isCanonical(child);
            String acp = AtomContainerPrinter.toString(child);
            System.out.println(isCanonical + "\t" + acp);
        }
    }

}
