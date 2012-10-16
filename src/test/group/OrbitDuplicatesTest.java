package test.group;

import generate.BaseAtomChildLister;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import test.AtomContainerPrinter;
import validate.SignatureValidator;

public class OrbitDuplicatesTest {
    
    public IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    public IAtomContainer makeParent() {
        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < 5; i++) {
            atomContainer.addAtom(builder.newInstance(IAtom.class, "C"));
        }
        atomContainer.addBond(0, 1, IBond.Order.DOUBLE);
        atomContainer.addBond(0, 2, IBond.Order.SINGLE);
        atomContainer.addBond(1, 3, IBond.Order.SINGLE);
        atomContainer.addBond(2, 4, IBond.Order.SINGLE);
        
        return atomContainer;
    }
    
    @Test
    public void augmentTest() {
        BaseAtomChildLister lister = new BaseAtomChildLister("CCCCCC");
        IAtomContainer parent = makeParent();
        IAtomContainer childA = lister.makeChild(parent, new int[] {1, 1, 0, 0, 2}, 5);
        IAtomContainer childB = lister.makeChild(parent, new int[] {1, 0, 2, 1, 0}, 5);
        AtomContainerPrinter.print(childA);
        AtomContainerPrinter.print(childB);
        SignatureValidator validator = new SignatureValidator();
        String parentSignature = new MoleculeSignature(parent).toCanonicalString();
        boolean canA = validator.isCanonical(parent, childA, parentSignature);
        System.out.println(canA);
        boolean canB = validator.isCanonical(parent, childB, parentSignature);
        System.out.println(canB);
    }

}
