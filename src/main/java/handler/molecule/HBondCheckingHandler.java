package handler.molecule;

import org.openscience.cdk.interfaces.IAtomContainer;

import handler.Handler;
import validate.HCountValidator;

public class HBondCheckingHandler implements Handler<IAtomContainer> {
    
    private HCountValidator hCountValidator;
    
    private Handler<IAtomContainer> delegate;
    
    public HBondCheckingHandler(String elementFormula, Handler<IAtomContainer> delegate) {
        this.delegate = delegate;
        this.hCountValidator = new HCountValidator(elementFormula);
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