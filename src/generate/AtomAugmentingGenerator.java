package generate;

import handler.GenerateHandler;
import handler.PrintStreamStringHandler;
import io.AtomContainerPrinter;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import app.AugmentingGenerator;
import validate.CanonicalValidator;
import validate.HCountValidator;
import validate.MoleculeValidator;
import validate.RefinementCanonicalValidator;
import validate.SignatureCanonicalValidator;
import canonical.ADPRCanonicalLabeller;
import canonical.SignatureCanonicalLabeller;

public class AtomAugmentingGenerator extends BaseAugmentingGenerator implements AugmentingGenerator {

    private GenerateHandler handler;

    private MoleculeValidator moleculeValidator;
    
    private CanonicalValidator canonicalValidator;
    
    private ChildLister childLister;
    
    public AtomAugmentingGenerator() {
        this(new PrintStreamStringHandler());
    }

    public AtomAugmentingGenerator(GenerateHandler handler) {
        this(handler, ListerMethod.FILTER, LabellerMethod.SIGNATURE, ValidatorMethod.REFINER);
    }
    
    public AtomAugmentingGenerator(ListerMethod listerMethod) {
        this(new PrintStreamStringHandler(), listerMethod, LabellerMethod.SIGNATURE, ValidatorMethod.REFINER);
    }
    
    public AtomAugmentingGenerator(ValidatorMethod validatorMethod) {
        this(new PrintStreamStringHandler(), ListerMethod.FILTER, LabellerMethod.SIGNATURE, validatorMethod);
    }
    
    public AtomAugmentingGenerator(GenerateHandler handler, 
                                   ListerMethod listerMethod, 
                                   LabellerMethod labellingMethod,
                                   ValidatorMethod validatorMethod) {
        this.handler = handler;
        if (listerMethod == ListerMethod.FILTER) {
            if (labellingMethod == LabellerMethod.SIGNATURE) {
                childLister = new AtomFilteringChildLister(new SignatureCanonicalLabeller());
            } else {
                childLister = new AtomFilteringChildLister(new ADPRCanonicalLabeller());
            }
        } else if (listerMethod == ListerMethod.SYMMETRIC) {
            childLister = new AtomSymmetricChildLister();
        } else {
            // XXX
        }
        
        moleculeValidator = new HCountValidator();
        if (validatorMethod == ValidatorMethod.REFINER) {
            canonicalValidator = new RefinementCanonicalValidator();
        } else if (validatorMethod == ValidatorMethod.SIGNATURE) {
            canonicalValidator = new SignatureCanonicalValidator();
        } else {
            // XXX
        }
    }
    
    public void setHCount(int hCount) {
        moleculeValidator.setHCount(hCount);
    }
    
    public void setElementSymbols(List<String> elementSymbols) {
        childLister.setElementSymbols(elementSymbols);
        moleculeValidator.setElementSymbols(elementSymbols);
    }
    
    public void extend(IAtomContainer parent, int size) {
        moleculeValidator.setImplicitHydrogens(parent);
        extend(parent, parent.getAtomCount(), size);
    }

    public void extend(IAtomContainer parent, int currentAtomIndex, int size) {
//    	System.out.println("parent " + AtomContainerPrinter.toString(parent));
        if (currentAtomIndex >= size) {
//        	System.out.println("current atom index >= size");
        	return;
        }
        if (!moleculeValidator.canExtend(parent)) {
//        	System.out.println("can't extend");
        	return;
        }
        
        moleculeValidator.checkConnectivity(parent);
        
        List<IAtomContainer> children = childLister.listChildren(parent, currentAtomIndex);
        for (IAtomContainer child : children) {
            if (canonicalValidator.isCanonical(child)) {
                if (moleculeValidator.isValidMol(child, size)) {
                    handler.handle(parent, child);
                }
                extend(child, currentAtomIndex + 1, size);
            }
        }
    }

    public List<String> getElementSymbols() {
        return childLister.getElementSymbols();
    }

	@Override
	public void finish() {
		handler.finish();
	}
}
