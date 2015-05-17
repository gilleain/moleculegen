package test.branch;

import io.AtomContainerPrinter;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import branch.AtomAugmentation;
import branch.AtomAugmentor;
import branch.Augmentation;

public class TestAtomAugmentor {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    @Test
    public void testCCAugmentations() {
        AtomAugmentor augmentor = new AtomAugmentor("CCC");
        AtomAugmentation start = new AtomAugmentation(AtomContainerPrinter.fromString("C0C1 0:1(1)", builder));
        for (Augmentation<IAtomContainer> augmentation : augmentor.augment(start)) {
            AtomContainerPrinter.print(augmentation.getAugmentedMolecule());
        }
    }

}
