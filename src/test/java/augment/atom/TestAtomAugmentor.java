package augment.atom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.silent.FastChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import augment.CanonicalChecker;
import augment.constraints.ElementConstraints;
import io.AtomContainerPrinter;

public class TestAtomAugmentor {

    public static IChemObjectBuilder getBuilder() {
        return  FastChemObjectBuilder.getInstance();   // changed SLewis for control
    }

    
    private List<AtomAugmentation> gen(String elementString, String startingGraph) {
        AtomAugmentor augmentor = new AtomAugmentor(elementString);
        ElementConstraints elements = new ElementConstraints(elementString);
        AtomAugmentation start = new AtomAugmentation(AtomContainerPrinter.fromString(startingGraph, getBuilder()), elements);
        return augmentor.augment(start);
    }
    
    private void print(Iterable<AtomAugmentation> augmentations) {
        int index = 0;
        CanonicalChecker<AtomAugmentation> checker = new AtomCanonicalChecker();
        for (AtomAugmentation augmentation : augmentations) {
//            System.out.print(index + "\t");
//            System.out.print(checker.isCanonical(augmentation) + "\t");
//            AtomContainerPrinter.print(augmentation.getBase());
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
    
    private void findDups(List<AtomAugmentation> augmentations) {
        Map<String, AtomAugmentation> canonical = new HashMap<String, AtomAugmentation>();
        CanonicalChecker<AtomAugmentation> checker = new AtomCanonicalChecker();
        for (AtomAugmentation augmentation : augmentations) {
            if (checker.isCanonical(augmentation)) {
                IAtomContainer mol = augmentation.getAugmentedObject(); 
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
    
    @Test
    public void testThreesFromCCBonds() {
        List<AtomAugmentation> augmentations = 
                new ArrayList<AtomAugmentation>();
        augmentations.addAll(gen("CCC", "C0C1 0:1(1)"));
        augmentations.addAll(gen("CCC", "C0C1 0:1(2)"));
        augmentations.addAll(gen("CCC", "C0C1 0:1(3)"));
        
        findDups(augmentations);
    }
    
    @Test
    public void testFoursFromCCCLine() {
        findDups(gen("CCCC", "C0C1C2 0:1(1),0:2(1)"));
    }
    
    @Test
    public void testFoursFromCCCTriangle() {
        findDups(gen("CCCC", "C0C1C2 0:1(1),0:2(1),1:2(1)"));
    }

}
