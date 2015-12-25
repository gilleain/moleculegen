package appbranch.augment.atom;

import org.openscience.cdk.interfaces.IAtomContainer;

import appbranch.augment.Augmentation;
import appbranch.augment.AugmentingGenerator;
import appbranch.augment.StateSource;
import appbranch.canonical.CanonicalChecker;
import appbranch.canonical.NonExpandingCanonicalChecker;
import appbranch.handler.Handler;
import validate.HCountValidator;


public class AtomGenerator implements AugmentingGenerator {
    
    private AtomAugmentor augmentor;
    
    private Handler handler;
    
    private int maxIndex;
    
    private HCountValidator hCountValidator;
    
    private CanonicalChecker<IAtomContainer, AtomExtension> canonicalChecker;
    
    public AtomGenerator(String elementFormula, Handler handler) {
        this.hCountValidator = new HCountValidator(elementFormula);
        StateSource<IAtomContainer, String> stateSource = new AtomOnlyStart(elementFormula);
        this.augmentor = new AtomAugmentor(hCountValidator.getElementSymbols(), stateSource);
        this.canonicalChecker = new NonExpandingCanonicalChecker();
        this.handler = handler;
        this.maxIndex = hCountValidator.getElementSymbols().size() - 1;
    }
    
    public void run() {
        augment(augmentor.getInitial(), 0);
    }
    
    public void run(IAtomContainer initial) {
        // XXX index = atomCount?
        augment(new AtomAugmentation(initial), initial.getAtomCount() - 1);  
    }
    
    private void augment(Augmentation<IAtomContainer, AtomExtension> parent, int index) {
        if (index >= maxIndex) {
            IAtomContainer atomContainer = parent.getBase();
            if (hCountValidator.isValidMol(atomContainer, maxIndex + 1)) {
                handler.handle(atomContainer);
            }
            return;
        }
        
        for (Augmentation<IAtomContainer, AtomExtension> augmentation : augmentor.augment(parent)) {
            if (canonicalChecker.isCanonical(augmentation)) {
                augment(augmentation, index + 1);
            }
        }
    }

    @Override
    public void finish() {
        handler.finish();
    }
}
