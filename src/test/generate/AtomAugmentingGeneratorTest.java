package test.generate;

import generate.AtomAugmentingGenerator;
import generate.AtomAugmentingGenerator.ListerMethod;
import handler.DataFormat;
import handler.PrintStreamHandler;

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
    
    public IAtomContainer makeCycloButane() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(0, 3, IBond.Order.SINGLE);
        ac.addBond(1, 2, IBond.Order.SINGLE);
        ac.addBond(2, 3, IBond.Order.SINGLE);
        return ac;
    }

    public IAtomContainer makeCycloPentane() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(0, 4, IBond.Order.SINGLE);
        ac.addBond(1, 2, IBond.Order.SINGLE);
        ac.addBond(2, 3, IBond.Order.SINGLE);
        ac.addBond(3, 4, IBond.Order.SINGLE);
        return ac;
    }
    
    /**
     * A molecule on 5 vertices that cannot be extended to 6 : C1=4C2=C3C=4C123.
     * 
     * @return
     */
    public IAtomContainer makeInextensibleFusedRingSystem() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(0, 2, IBond.Order.SINGLE);
        ac.addBond(1, 2, IBond.Order.SINGLE);
        ac.addBond(0, 3, IBond.Order.DOUBLE);
        ac.addBond(1, 3, IBond.Order.SINGLE);
        ac.addBond(1, 4, IBond.Order.SINGLE);
        ac.addBond(2, 4, IBond.Order.DOUBLE);
        ac.addBond(3, 4, IBond.Order.SINGLE);
        return ac;
    }
    
    public IAtomContainer makeButene() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addBond(0, 1, IBond.Order.DOUBLE);
        ac.addBond(0, 2, IBond.Order.SINGLE);
        ac.addBond(2, 3, IBond.Order.SINGLE);
        return ac;
    }
    
    public IAtomContainer makeButane() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(0, 2, IBond.Order.SINGLE);
        ac.addBond(2, 3, IBond.Order.SINGLE);
        return ac;
    }
    
    public IAtomContainer makePentene() {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addBond(0, 1, IBond.Order.DOUBLE);
        ac.addBond(0, 2, IBond.Order.SINGLE);
        ac.addBond(1, 3, IBond.Order.SINGLE);
        ac.addBond(2, 4, IBond.Order.SINGLE);
        return ac;
    }

    public void testNFromSingleDoubleTriple(String elementString, int n, int hCount) {
        IAtomContainer ccSingle = makeCCEdge(IBond.Order.SINGLE);
        IAtomContainer ccDouble = makeCCEdge(IBond.Order.DOUBLE);
        IAtomContainer ccTriple = makeCCEdge(IBond.Order.TRIPLE);
        PrintStreamHandler handler = new PrintStreamHandler(System.out, DataFormat.SMILES);
//        PrintStreamHandler handler = new PrintStreamHandler(System.out, OutputFormat.SIGNATURE);
//        AtomAugmentingGenerator generator = new AtomAugmentingGenerator(handler, ListerMethod.FILTER);
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator(handler, ListerMethod.SYMMETRIC);
        generator.setHCount(hCount);
        generator.setElementString(elementString);
        
        generator.extend(ccSingle, 2, n);
//        System.out.println("--");
        generator.extend(ccDouble, 2, n);
//        System.out.println("--");
        generator.extend(ccTriple, 2, n);
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
        testNFromSingleDoubleTriple("CCCC", 4, 8);
    }
    
    @Test
    public void testFivesFromSingleDoubleAndTripleEdges() {
        testNFromSingleDoubleTriple("CCCCC", 5, 10);
    }
    
    @Test
    public void testSixesFromSingleDoubleAndTripleEdges() {
        testNFromSingleDoubleTriple("CCCCCC", 6, 12);
    }
    
    @Test
    public void testSevensFromSingleDoubleAndTripleEdges() {
        testNFromSingleDoubleTriple("CCCCCCC", 7, 14);
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
        generator.setHCount(10);
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
    
    @Test
    public void extendCyclobutane() {
        IAtomContainer cyclobutane = makeCycloButane();
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementString("CCCCC");
        generator.extend(cyclobutane, 4, 5);
    }
    
    @Test
    public void extendButene() {
        IAtomContainer butene = makeButene();
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementString("CCCCC");
        generator.extend(butene, 4, 5);
    }
    
    @Test
    public void extendButane() {
        IAtomContainer butane = makeButane();
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator(ListerMethod.SYMMETRIC);
        generator.setElementString("CCCCC");
        generator.extend(butane, 4, 5);
    }
    
    @Test
    public void extendCyclopentane() {
        IAtomContainer cyclopentane = makeCycloPentane();
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementString("CCCCCC");
        generator.extend(cyclopentane, 5, 6);
    }
    
    @Test
    public void extendPentene() {
        IAtomContainer pentene = makePentene();
        PrintStreamHandler handler = new PrintStreamHandler(System.out, DataFormat.SIGNATURE);
//        PrintStreamHandler handler = new PrintStreamHandler(System.out, OutputFormat.SMILES);
//        AtomAugmentingGenerator generator = new AtomAugmentingGenerator(handler, ListerMethod.SYMMETRIC);
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator(handler, ListerMethod.FILTER);
        generator.setElementString("CCCCCC");
        generator.extend(pentene, 5, 6);
    }
    
    @Test
    public void extendFusedRingSystem() {
        IAtomContainer fusedSystem = makeInextensibleFusedRingSystem();
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementString("CCCCCC");
        generator.extend(fusedSystem, 5, 6);
    }

}
