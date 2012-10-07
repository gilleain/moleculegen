package test.generate;

import generate.AtomAugmentingGenerator;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class AtomAugmentingGeneratorTest {
    
    public IAtomContainer makeSingleC() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        return ac;
    }
    
    public IAtomContainer makeSingleCCEdge() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        return ac;
    }
    
    @Test
    public void testFoursFromSingleEdge() {
        IAtomContainer initial = makeSingleCCEdge();
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementString("CCCC");
        generator.extend(initial, 2, 4);
    }
    
    @Test
    public void testFoursFromSingleAtom() {
        IAtomContainer initial = makeSingleC();
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementString("CCCC");
        generator.extend(initial, 1, 4);
    }
    
    @Test
    public void testFivesFromSingleAtom() {
        IAtomContainer initial = makeSingleC();
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementString("CCCCC");
        generator.extend(initial, 1, 5);
    }

}
