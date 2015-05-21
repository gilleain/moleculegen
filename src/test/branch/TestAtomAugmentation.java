package test.branch;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openscience.cdk.group.AtomContainerPrinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import branch.AtomAugmentation;

/**
 * Test augmentation of molecules by single atoms and sets of bonds.
 * 
 * @author maclean
 *
 */
public class TestAtomAugmentation {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    private IAtomContainer make(String acpString) {
        return AtomContainerPrinter.fromString(acpString, builder);
    }
    
    private AtomAugmentation makeAugmentation(IAtomContainer mol, String elementSymbol, int... points) {
        return new AtomAugmentation(mol, builder.newInstance(IAtom.class, elementSymbol), points);
    }
    
    @Test
    public void testCanonical() {
        IAtomContainer molA = make("C0C1C2C3 0:1(1),0:2(1),1:3(1),2:3(1)");
        AtomAugmentation augmentationA = makeAugmentation(molA, "C", 1, 1, 0, 0);
        IAtomContainer augmentedMolA = augmentationA.getAugmentedMolecule();
        System.out.println(augmentationA.isCanonical() + " " + AtomContainerPrinter.toString(augmentedMolA));
        
        IAtomContainer molB = make("C0C1C2C3 0:1(1),0:2(1),0:3(1),1:2(1)");
        AtomAugmentation augmentationB = makeAugmentation(molB, "C", 0, 1, 0, 1);
        IAtomContainer augmentedMolB = augmentationB.getAugmentedMolecule();
        System.out.println(augmentationB.isCanonical() + " " + AtomContainerPrinter.toString(augmentedMolB));
    }
    
    @Test
    public void testCanonicalFromSingle() {
        IAtomContainer molA = make("C0C1 0:1(1)");
        AtomAugmentation augA = makeAugmentation(molA, "C", 2, 1);
        IAtomContainer augMolA = augA.getAugmentedMolecule();
        System.out.println(augA.isCanonical() + "\t" + AtomContainerPrinter.toString(augMolA));
        
        IAtomContainer molB = make("C0C1 0:1(2)");
        AtomAugmentation augB = makeAugmentation(molB, "C", 1, 1);
        IAtomContainer augMolB = augB.getAugmentedMolecule();
        System.out.println(augB.isCanonical() + "\t" + AtomContainerPrinter.toString(augMolB));
    }
    
    @Test
    public void testGetAugmentedMolecule() {
        IAtomContainer mol = make("C0C1C2 0:1(1),0:2(1)");
        AtomAugmentation augmentation = makeAugmentation(mol, "C", 1, 0, 1);
        IAtomContainer augmentedMol = augmentation.getAugmentedMolecule();
        AtomContainerPrinter.print(augmentedMol);
        IBond addedBond = augmentedMol.getBond(2);
        assertEquals("Added bond 1", IBond.Order.SINGLE, addedBond.getOrder());
    }

}
