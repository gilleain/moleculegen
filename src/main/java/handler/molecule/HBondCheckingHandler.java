package handler.molecule;

import org.openscience.cdk.interfaces.IAtomContainer;

import app.FormulaParser;
import augment.chem.HCountValidator;
import handler.Handler;

public class HBondCheckingHandler implements Handler<IAtomContainer> {
    
    private HCountValidator hCountValidator;
    
    private Handler<IAtomContainer> delegate;
    
    public HBondCheckingHandler(String elementFormula, Handler<IAtomContainer> delegate) {
        this.delegate = delegate;
        this.hCountValidator = new HCountValidator(new FormulaParser(elementFormula));
    }

    @Override
    public void handle(IAtomContainer atomContainer) {
        int size = atomContainer.getAtomCount();    // can assume this?
        if (hCountValidator.isValidMol(atomContainer, size)) {
            delegate.handle(atomContainer);
        }
    }

    @Override
    public void finish() {
        delegate.finish();
    }

}
