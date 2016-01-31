package handler;

import org.openscience.cdk.interfaces.IAtomContainer;

import validate.HCountValidator;

public class HBondCheckingHandler implements Handler {
    
    private HCountValidator hCountValidator;
    
    private Handler delegate;
    
    public HBondCheckingHandler(String elementFormula, Handler delegate) {
        this.hCountValidator = new HCountValidator(elementFormula);
        this.delegate = delegate;
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
        // TODO Auto-generated method stub
        
    }

}
