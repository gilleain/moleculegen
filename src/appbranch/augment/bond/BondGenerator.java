package appbranch.augment.bond;

import org.openscience.cdk.interfaces.IAtomContainer;

import appbranch.augment.Augmentation;
import appbranch.augment.InitialStateSource;
import appbranch.canonical.CanonicalChecker;
import appbranch.handler.Handler;
import validate.HCountValidator;

public class BondGenerator {
    
    private Handler handler;
    
    private BondAugmentor augmentor;
    
    private InitialStateSource<IAtomContainer> initialStateSource;
    
    private CanonicalChecker<IAtomContainer, BondExtension> canonicalChecker;
    
    private HCountValidator hCountValidator;
    
    public BondGenerator(String elementFormula, Handler handler) {
        this(elementFormula, new BondOnlyStart(elementFormula), handler);
    }
    
    public BondGenerator(String elementFormula, 
            InitialStateSource<IAtomContainer> initialStateSource, Handler handler) {
        this.handler = handler;
        this.initialStateSource = initialStateSource;
        this.augmentor = new BondAugmentor(elementFormula);
        hCountValidator = new HCountValidator(elementFormula);
        canonicalChecker = new BondCanonicalChecker();
    }
    
    public void run() {
        for (IAtomContainer startingStructure : initialStateSource.get()) {
            augment(new BondAugmentation(startingStructure));   // XXX null Bond Extension
        }
    }
    
    public void run(IAtomContainer initial) {
        augment(new BondAugmentation(initial));  
    }

    private void augment(Augmentation<IAtomContainer, BondExtension> parent) {
//        System.out.println("Augmenting " + io.AtomContainerPrinter.toString(parent.getBase()));
        if (augmentor.isComplete(parent)) {
            IAtomContainer atomContainer = parent.getBase();
            if (hCountValidator.isValidMol(atomContainer, atomContainer.getAtomCount())) {
                handler.handle(atomContainer);
            }
        }
        
        for (Augmentation<IAtomContainer, BondExtension> augmentation : augmentor.augment(parent)) {
            if (canonicalChecker.isCanonical(augmentation)) {
                augment(augmentation);
            }
        }
    }
}
