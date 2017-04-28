package augment.constraints;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

public class ElementConstraintSource implements Serializable {
    
    private static final long serialVersionUID = -4695344175108302615L;

    public static IChemObjectBuilder getBuilder() {
        return SilentChemObjectBuilder.getInstance();
    }
    
    private final ElementConstraints elementConstraints;
    
    public ElementConstraintSource(ElementConstraints elementConstraints) {
        this.elementConstraints = elementConstraints;
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
        IChemObjectBuilder builder = getBuilder();
        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        atomContainer.addAtom(builder.newInstance(IAtom.class, symbol));
        return atomContainer;
    }

}
