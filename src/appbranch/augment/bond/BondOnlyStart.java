package appbranch.augment.bond;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import appbranch.FormulaParser;
import appbranch.augment.InitialStateSource;

public class BondOnlyStart implements InitialStateSource<IAtomContainer> {
    
    private final List<String> elementSymbols;
    
    private final IChemObjectBuilder builder;
    
    public BondOnlyStart(String elementFormula) {
        builder = SilentChemObjectBuilder.getInstance();
        FormulaParser formulaParser = new FormulaParser(elementFormula);
        elementSymbols = formulaParser.getElementSymbols();
    }

    @Override
    public Iterable<IAtomContainer> get() {
        List<IAtomContainer> singletonList = new ArrayList<IAtomContainer>();
//        singletonList.add(makeBond(IBond.Order.SINGLE));
//        singletonList.add(makeBond(IBond.Order.DOUBLE));
//        singletonList.add(makeBond(IBond.Order.TRIPLE));
        singletonList.add(makeAtom());
        return singletonList;
    }
    
    private IAtomContainer makeAtom() {
        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        atomContainer.addAtom(builder.newInstance(IAtom.class, elementSymbols.get(0)));
        atomContainer.setProperty("IS_CONNECTED", false);
        return atomContainer;
    }
    
    
    private IAtomContainer makeBond(IBond.Order order) {
        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        atomContainer.addAtom(builder.newInstance(IAtom.class, elementSymbols.get(0)));
        atomContainer.addAtom(builder.newInstance(IAtom.class, elementSymbols.get(1)));
        atomContainer.addBond(0, 1, order);
        // XXX - why is this false?
        atomContainer.setProperty("IS_CONNECTED", false);
        return atomContainer;
    }
}
