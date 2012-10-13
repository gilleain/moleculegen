package test.generate;

import generate.AtomAugmentingGenerator;
import generate.AtomAugmentingGenerator.ListerMethod;
import handler.CountingHandler;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

public class BaseTest {
    
    public IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    public IAtomContainer makeCCEdge(IBond.Order order) {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addBond(0, 1, order);
        return ac;
    }
    
    public List<String> elementSymbols(String elementString) {
        List<String> elementSymbols = new ArrayList<String>();
        for (int i = 0; i < elementString.length(); i++) {
            elementSymbols.add(String.valueOf(elementString.charAt(i)));
        }
        return elementSymbols;
    }
    
    public int countNFromSingleDoubleTriple(String formulaString, ListerMethod listerMethod) {
        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(formulaString, builder);
        List<String> elementSymbols = new ArrayList<String>();
        int hCount = 0;
        for (IIsotope element : formula.isotopes()) {   // isotopes are not elements, I know...
            String elementSymbol = element.getSymbol();
            int count = formula.getIsotopeCount(element);
            if (elementSymbol.equals("H")) {
                hCount = count;
            } else {
                for (int i = 0; i < count; i++) {
                    elementSymbols.add(elementSymbol);
                }
            }
        }
        int n = elementSymbols.size();
        
        IAtomContainer ccSingle = makeCCEdge(IBond.Order.SINGLE);
        IAtomContainer ccDouble = makeCCEdge(IBond.Order.DOUBLE);
        IAtomContainer ccTriple = makeCCEdge(IBond.Order.TRIPLE);
        CountingHandler handler = new CountingHandler();
        
        AtomAugmentingGenerator generator = new AtomAugmentingGenerator(handler, listerMethod);
        generator.setHCount(hCount);
        generator.setElementSymbols(elementSymbols);
        
        generator.extend(ccSingle, 2, n);
        generator.extend(ccDouble, 2, n);
        generator.extend(ccTriple, 2, n);
        
        return handler.getCount();
    }
}