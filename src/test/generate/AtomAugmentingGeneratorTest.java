package test.generate;

import generate.AtomAugmentingGenerator;
import generate.LabellerMethod;
import generate.ListerMethod;
import generate.ValidatorMethod;
import handler.DataFormat;
import handler.PrintStreamStringHandler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class AtomAugmentingGeneratorTest extends BaseTest {
    
    public IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    public IAtomContainer makeSingleC() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setImplicitHydrogenCount(4);
        ac.addAtom(atom);
        return ac;
    }
    
    public IAtomContainer makePropene() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addBond(0, 1, IBond.Order.DOUBLE);
        ac.addBond(0, 2, IBond.Order.SINGLE);
        return ac;
    }
    
    public IAtomContainer makeCycloPropane() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(0, 2, IBond.Order.SINGLE);
        ac.addBond(1, 2, IBond.Order.SINGLE);
        return ac;
    }
    
    public IAtomContainer makeCycloButane() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(0, 3, IBond.Order.SINGLE);
        ac.addBond(1, 2, IBond.Order.SINGLE);
        ac.addBond(2, 3, IBond.Order.SINGLE);
        return ac;
    }

    public IAtomContainer makeCycloPentane() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
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
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
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
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addBond(0, 1, IBond.Order.DOUBLE);
        ac.addBond(0, 2, IBond.Order.SINGLE);
        ac.addBond(2, 3, IBond.Order.SINGLE);
        return ac;
    }
    
    public IAtomContainer makeButane() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(0, 2, IBond.Order.SINGLE);
        ac.addBond(2, 3, IBond.Order.SINGLE);
        return ac;
    }
    
    public IAtomContainer makePentene() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
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
        PrintStreamStringHandler handler = new PrintStreamStringHandler(System.out, DataFormat.SMILES);
//        PrintStreamHandler handler = new PrintStreamHandler(System.out, OutputFormat.SIGNATURE);
//        AtomAugmentingGenerator generator = new AtomAugmentingGenerator(handler, ListerMethod.FILTER);
        AtomAugmentingGenerator generator = 
            new AtomAugmentingGenerator(handler, ListerMethod.SYMMETRIC, LabellerMethod.SIGNATURE, ValidatorMethod.SIGNATURE);
        generator.setHCount(hCount);
        List<String> elementSymbols = new ArrayList<String>();
        for (int i = 0; i < elementString.length(); i++) {
            elementSymbols.add(String.valueOf(elementString.charAt(i)));
        }
        generator.setElementSymbols(elementSymbols);
        
        generator.extend(ccSingle, 2, n);
//        System.out.println("--");
        generator.extend(ccDouble, 2, n);
//        System.out.println("--");
        generator.extend(ccTriple, 2, n);
    }
    
    public void testFromEdge(IBond.Order order, String elementString) {
        IAtomContainer initial = makeCCEdge(order);
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementSymbols(elementSymbols(elementString));
        generator.extend(initial, 2, elementString.length());
    }
    
    @Test
    public void testThreesFromSingleEdge() {
        testFromEdge(IBond.Order.SINGLE, "CCC");
    }
    
    @Test
    public void testThreesFromDoubleEdge() {
        testFromEdge(IBond.Order.DOUBLE, "CCC");
    }
    
    @Test
    public void testFoursFromSingleEdge() {
        testFromEdge(IBond.Order.SINGLE, "CCCC");
    }
    
    @Test
    public void testThreesFromSingleDoubleAndTripleEdges() {
        testNFromSingleDoubleTriple("CCC", 3, 0);
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
    
    
    public void testFromSingleAtom(String elementString, 
                                   int currentIndex, 
                                   int n,
                                   int hCount) {
        List<String> elementSymbols = elementSymbols(elementString);
        IAtomContainer initial = makeSingleC();
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementSymbols(elementSymbols);
        generator.setHCount(hCount);
        generator.extend(initial, currentIndex, n);
    }
    
    public void extend(IAtomContainer initial, String elementString) {
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator();
        generator.setElementSymbols(elementSymbols(elementString));
        generator.extend(initial, 3, elementString.length());
    }
    
    @Test
    public void testThreesFromSingleAtom() {
        testFromSingleAtom("CCC", 1, 3, 0);
    }
    
    @Test
    public void testFoursFromSingleAtom() {
        testFromSingleAtom("CCCC", 1, 4, 8);
    }
    
    @Test
    public void testFivesFromSingleAtom() {
        testFromSingleAtom("CCCCC", 1, 5, 10);
    }
    
    @Test
    public void extendPropene() {
        extend(makePropene(), "CCCC");
    }
    
    @Test
    public void extendCyclopropane() {
        extend(makeCycloPropane(), "CCCC");
    }
    
    @Test
    public void extendCyclobutane() {
        extend(makeCycloButane(), "CCCCC");
    }
    
    @Test
    public void extendButene() {
        extend(makeButene(), "CCCCC");
    }
    
    @Test
    public void extendButane() {
        extend(makeButane(), "CCCCC");
    }
    
    @Test
    public void extendCyclopentane() {
        extend(makeCycloPentane(), "CCCCCC");
    }
    
    @Test
    public void extendPentene() {
        extend(makePentene(), "CCCCCC");
    }
    
    @Test
    public void extendFusedRingSystem() {
        extend(makeInextensibleFusedRingSystem(), "CCCCCC");
    }

}
