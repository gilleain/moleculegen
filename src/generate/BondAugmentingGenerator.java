package generate;

import handler.GenerateHandler;
import handler.PrintStreamStringHandler;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import app.AugmentingGenerator;
import validate.BondCanonicalValidator;
import validate.HCountValidator;
import validate.MoleculeValidator;

public class BondAugmentingGenerator extends BaseAugmentingGenerator implements AugmentingGenerator {
    
    private GenerateHandler handler;
    
    private MoleculeValidator hCountValidator;
    
    private BondChildLister childLister;
    
    private BondCanonicalValidator canonicalValidator;
    
    public BondAugmentingGenerator() {
        this(new PrintStreamStringHandler());
    }

    public BondAugmentingGenerator(GenerateHandler handler) {
        this.handler = handler;
        hCountValidator = new HCountValidator();
        childLister = new BondChildLister();
        canonicalValidator = new BondCanonicalValidator();
    }
    
    public void setHCount(int hCount) {
        hCountValidator.setHCount(hCount);
    }
    
    public void setElementSymbols(List<String> elementSymbols) {
        childLister.setElementSymbols(elementSymbols);
    }
    
    public void extend(IAtomContainer parent, int size) {
        if (childLister.isFinished(parent, size)) {
            return;
        } else {
            for (IAtomContainer child : childLister.listChildren(parent, size)) {
                if (canonicalValidator.isCanonical(child)) {
                    if (hCountValidator.isValidMol(child, size)) {
                        handler.handle(parent, child);
                    }
                    extend(child, size);
                }
            }
        }
    }

    @Override
    public List<String> getElementSymbols() {
        return childLister.getElementSymbols();
    }

	@Override
	public void finish() {
		handler.finish();
	}
}
