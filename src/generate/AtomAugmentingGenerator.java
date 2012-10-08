package generate;

import handler.GenerateHandler;
import handler.PrintStreamHandler;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import validate.MoleculeValidator;
import validate.SimpleValidator;

public class AtomAugmentingGenerator {

    private GenerateHandler handler;

    private MoleculeValidator validator;
    
    private ChildLister childLister;
    
    public enum ListerMethod {
        FILTER,
        SYMMETRIC
    };

    public AtomAugmentingGenerator() {
        this(new PrintStreamHandler());
    }

    public AtomAugmentingGenerator(GenerateHandler handler) {
        this(handler, ListerMethod.FILTER);
    }
    
    public AtomAugmentingGenerator(GenerateHandler handler, ListerMethod method) {
        this.handler = handler;
        if (method == ListerMethod.FILTER) {
            childLister = new AtomFilteringChildLister();
        } else if (method == ListerMethod.SYMMETRIC) {
            childLister = new AtomSymmetricChildLister();
        } else {
            // XXX
        }
        validator = new SimpleValidator((SignatureChildLister)childLister);
    }
    
    public void setElementString(String elementString) {
        childLister.setElementString(elementString);
    }

    public void extend(IAtomContainer parent, int currentAtomIndex, int size) {
        if (currentAtomIndex >= size) return;
        
        List<IAtomContainer> children = childLister.listChildren(parent, currentAtomIndex);

        for (IAtomContainer child : children) {
            if (validator.isCanonical(parent, child)) {
                if (validator.isValidMol(child, size)) {
                    handler.handle(parent, child);
                }
                extend(child, currentAtomIndex + 1, size);
            }
        }
    }
}
