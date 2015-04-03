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
    
    private IAtomContainer makeTestMolecule() {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        return mol;
    }
    
    @Test
    public void testGetAugmentedMolecule() {
        IAtomContainer mol = makeTestMolecule();
        IAtom atom = builder.newInstance(IAtom.class, "C");
        int[] augmentationPoints = new int[] {1, 0, 1};
        AtomAugmentation augmentation = new AtomAugmentation(mol, atom, augmentationPoints);
        IAtomContainer augmentedMol = augmentation.getAugmentedMolecule();
        AtomContainerPrinter.print(augmentedMol);
        IBond addedBond = augmentedMol.getBond(2);
        assertEquals("Added bond 1", IBond.Order.SINGLE, addedBond.getOrder());
    }

}
