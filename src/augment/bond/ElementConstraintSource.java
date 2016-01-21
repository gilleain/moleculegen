package augment.bond;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class ElementConstraintSource {
    
    private final ElementConstraints elementConstraints;
    private final IChemObjectBuilder builder;
    
    public ElementConstraintSource(ElementConstraints elementConstraints) {
        this.elementConstraints = elementConstraints;
        builder = SilentChemObjectBuilder.getInstance();
    }

    public Iterable<IAtomContainer> get() {
        List<IAtomContainer> list = new ArrayList<IAtomContainer>();
//        for (String symbol : elementConstraints) {
//            list.add(makeAtom(symbol));
//        }
        list.add(makeAtom(elementConstraints.iterator().next()));
        return list;
    }
    
    private IAtomContainer makeAtom(String symbol) {
        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        atomContainer.addAtom(builder.newInstance(IAtom.class, symbol));
        atomContainer.setProperty("IS_CONNECTED", false);
        return atomContainer;
    }

}
