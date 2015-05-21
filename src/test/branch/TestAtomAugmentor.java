package test.branch;

import io.AtomContainerPrinter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import branch.AtomAugmentation;
import branch.AtomAugmentor;
import branch.Augmentation;

public class TestAtomAugmentor {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    private List<Augmentation<IAtomContainer>> gen(String elementString, String startingGraph) {
        AtomAugmentor augmentor = new AtomAugmentor("CCC");
        AtomAugmentation start = new AtomAugmentation(AtomContainerPrinter.fromString(startingGraph, builder));
        return augmentor.augment(start);
    }
    
    private void print(Iterable<Augmentation<IAtomContainer>> augmentations) {
        int index = 0;
        for (Augmentation<IAtomContainer> augmentation : augmentations) {
            System.out.print(index + "\t");
            System.out.print(augmentation.isCanonical() + "\t");
            AtomContainerPrinter.print(augmentation.getAugmentedMolecule());
            index++;
        }
    }
    
    @Test
    public void testCCSingle() {
        print(gen("CCC", "C0C1 0:1(1)"));
    }
    
    @Test
    public void testCCDouble() {
        print(gen("CCC", "C0C1 0:1(2)"));
    }
    
    @Test
    public void testCCTriple() {
        print(gen("CCC", "C0C1 0:1(3)"));
    }
    
    @Test
    public void testCCBonds() {
        List<Augmentation<IAtomContainer>> augmentations = new ArrayList<Augmentation<IAtomContainer>>();
        augmentations.addAll(gen("CCC", "C0C1 0:1(1)"));
        augmentations.addAll(gen("CCC", "C0C1 0:1(2)"));
        augmentations.addAll(gen("CCC", "C0C1 0:1(3)"));
        
        Map<String, Augmentation<IAtomContainer>> canonical = new HashMap<String, Augmentation<IAtomContainer>>();
        for (Augmentation<IAtomContainer> augmentation : augmentations) {
            if (augmentation.isCanonical()) {
                IAtomContainer mol = augmentation.getAugmentedMolecule(); 
                String sig = new MoleculeSignature(mol).toCanonicalString();
                if (canonical.containsKey(sig)) {
                    System.out.println("dup " + AtomContainerPrinter.toString(mol));
                } else {
                    canonical.put(sig, augmentation);
                }
            }
        }
        print(canonical.values());
    }

}
