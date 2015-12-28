package test.branch;

import java.util.List;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import appbranch.augment.Augmentation;
import appbranch.augment.bond.BondAugmentation;
import appbranch.augment.bond.BondAugmentor;
import appbranch.augment.bond.BondExtension;
import io.AtomContainerPrinter;

public class TestBondAugmentor {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    private List<Augmentation<IAtomContainer, BondExtension>> gen(String elementFormula, String startingGraph) {
        BondAugmentor augmentor = new BondAugmentor(elementFormula);
        BondAugmentation start = new BondAugmentation(AtomContainerPrinter.fromString(startingGraph, builder));
        return augmentor.augment(start);
    }
    
    private void print(Iterable<Augmentation<IAtomContainer, BondExtension>> augmentations) {
        int index = 0;
//        CanonicalChecker<IAtomContainer, AtomExtension> checker = new NonExpandingCanonicalChecker();
        for (Augmentation<IAtomContainer, BondExtension> augmentation : augmentations) {
            System.out.print(index + "\t");
//            System.out.print(checker.isCanonical(augmentation) + "\t");
            AtomContainerPrinter.print(augmentation.getBase());
            index++;
        }
    }
    
    @Test
    public void testCCSingle() {
        print(gen("C3", "C0C1 0:1(1)"));
    }
    
    @Test
    public void testMultipleDoubleBondStart() {
        print(gen("C4", "C0C1C2C3 0:1(2),1:2(1),2:3(2)"));
    }

}
