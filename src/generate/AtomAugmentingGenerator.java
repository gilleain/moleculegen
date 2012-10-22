package generate;

import handler.GenerateHandler;
import handler.PrintStreamStringHandler;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import validate.CanonicalValidator;
import validate.HCountValidator;
import validate.MoleculeValidator;
import validate.RefinementCanonicalValidator;
import validate.SignatureCanonicalValidator;

public class AtomAugmentingGenerator extends BaseAugmentingGenerator implements AugmentingGenerator {

    private GenerateHandler handler;

    private MoleculeValidator hCountValidator;
    
    private CanonicalValidator canonicalValidator;
    
    private ChildLister childLister;
    
    public AtomAugmentingGenerator() {
        this(new PrintStreamStringHandler());
    }

    public AtomAugmentingGenerator(GenerateHandler handler) {
        this(handler, ListerMethod.FILTER, ValidatorMethod.REFINER);
    }
    
    public AtomAugmentingGenerator(ListerMethod listerMethod) {
        this(new PrintStreamStringHandler(), listerMethod, ValidatorMethod.REFINER);
    }
    
    public AtomAugmentingGenerator(ValidatorMethod validatorMethod) {
        this(new PrintStreamStringHandler(), ListerMethod.FILTER, validatorMethod);
    }
    
    public AtomAugmentingGenerator(GenerateHandler handler, 
                                   ListerMethod listerMethod, 
                                   ValidatorMethod validatorMethod) {
        this.handler = handler;
        if (listerMethod == ListerMethod.FILTER) {
            childLister = new AtomFilteringChildLister();
        } else if (listerMethod == ListerMethod.SYMMETRIC) {
            childLister = new AtomSymmetricChildLister();
        } else {
            // XXX
        }
        
        hCountValidator = new HCountValidator();
        if (validatorMethod == ValidatorMethod.REFINER) {
            canonicalValidator = new RefinementCanonicalValidator();
        } else if (validatorMethod == ValidatorMethod.SIGNATURE) {
            canonicalValidator = new SignatureCanonicalValidator();
        } else {
            // XXX
        }
    }
    
    public void setHCount(int hCount) {
        hCountValidator.setHCount(hCount);
    }
    
    public void setElementSymbols(List<String> elementSymbols) {
        childLister.setElementSymbols(elementSymbols);
        hCountValidator.setElementSymbols(elementSymbols);
    }
    
    public void extend(IAtomContainer parent, int size) {
        hCountValidator.setImplicitHydrogens(parent);
        extend(parent, parent.getAtomCount(), size);
    }

    public void extend(IAtomContainer parent, int currentAtomIndex, int size) {
        if (currentAtomIndex >= size) return;
        if (!hCountValidator.canExtend(parent)) return;
        
        List<IAtomContainer> children = childLister.listChildren(parent, currentAtomIndex);
        for (IAtomContainer child : children) {
            if (canonicalValidator.isCanonical(child)) {
                if (hCountValidator.isValidMol(child, size)) {
                    handler.handle(parent, child);
                }
                extend(child, currentAtomIndex + 1, size);
            }
        }
    }

    public List<String> getElementSymbols() {
        return childLister.getElementSymbols();
    }
}
