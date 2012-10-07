package test.validate;

import generate.AtomSymmetricChildLister;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import test.AtomContainerPrinter;
import validate.SimpleValidator;

public class SimpleValidatorTest {
    
    public IAtomContainer make3Star() {
        IAtomContainer atomContainer = new AtomContainer();
        atomContainer.addAtom(new Atom("C"));
        atomContainer.addAtom(new Atom("C"));
        atomContainer.addAtom(new Atom("C"));
        atomContainer.addAtom(new Atom("C"));
        atomContainer.addBond(0, 1, IBond.Order.SINGLE);
        atomContainer.addBond(0, 2, IBond.Order.SINGLE);
        atomContainer.addBond(0, 3, IBond.Order.SINGLE);
        return atomContainer;
    }
    
    @Test
    public void canonicalChecking() {
        IAtomContainer parent = make3Star();
        AtomContainerPrinter.print(parent);
        AtomSymmetricChildLister lister = new AtomSymmetricChildLister("CCCCC");
        SimpleValidator validator = new SimpleValidator(lister);
        int len = parent.getAtomCount();
        for (IAtomContainer child : lister.listChildren(parent, len)) {
            boolean isCanonical = validator.isCanonical(parent, child);
            String acp = AtomContainerPrinter.toString(child);
            System.out.println(isCanonical + "\t" + acp);
        }
    }

}
