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
    
    public IAtomContainer makeCCEdge(IBond.Order order) {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addBond(0, 1, order);
        return ac;
    }
    
    public IAtomContainer makePropene() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addBond(0, 1, IBond.Order.DOUBLE);
        ac.addBond(0, 2, IBond.Order.SINGLE);
        return ac;
    }
    
    public IAtomContainer makeCycloPropane() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(0, 2, IBond.Order.SINGLE);
        ac.addBond(1, 2, IBond.Order.SINGLE);
        return ac;
    }
    
    @Test
    public void testFoursFromSingleEdge() {
        IAtomContainer initial = makeCCEdge(IBond.Order.SINGLE);
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementString("CCCC");
        generator.extend(initial, 2, 4);
    }
    
    @Test
    public void testFoursFromSingleDoubleAndTripleEdges() {
        IAtomContainer ccSingle = makeCCEdge(IBond.Order.SINGLE);
        IAtomContainer ccDouble = makeCCEdge(IBond.Order.DOUBLE);
        IAtomContainer ccTriple = makeCCEdge(IBond.Order.TRIPLE);
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementString("CCCC");
        generator.extend(ccSingle, 2, 4);
        generator.extend(ccDouble, 2, 4);
        generator.extend(ccTriple, 2, 4);
    }
    
    @Test
    public void testThreesFromSingleAtom() {
        IAtomContainer initial = makeSingleC();
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementString("CCC");
        generator.extend(initial, 1, 3);
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
    
    @Test
    public void extendPropene() {
        IAtomContainer propene = makePropene();
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementString("CCCC");
        generator.extend(propene, 3, 4);
    }
    
    @Test
    public void extendCyclopropane() {
        IAtomContainer propene = makeCycloPropane();
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementString("CCCC");
        generator.extend(propene, 3, 4);
    }

}
