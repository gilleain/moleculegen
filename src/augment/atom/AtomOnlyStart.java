package augment.atom;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import app.FormulaParser;
import augment.InitialStateSource;

public class AtomOnlyStart implements InitialStateSource<IAtomContainer> {
    
    private final List<String> elementSymbols;
    
    private final IChemObjectBuilder builder; 
    
    public AtomOnlyStart(String elementFormula) {
        builder = SilentChemObjectBuilder.getInstance();
        FormulaParser formulaParser = new FormulaParser(elementFormula);
        elementSymbols = formulaParser.getElementSymbols();
    }

    @Override
    public Iterable<IAtomContainer> get() {
        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        atomContainer.addAtom(builder.newInstance(IAtom.class, elementSymbols.get(0)));
        atomContainer.setProperty("IS_CONNECTED", false);
        List<IAtomContainer> singletonList = new ArrayList<IAtomContainer>();
        singletonList.add(atomContainer);
        return singletonList;
    }
}
