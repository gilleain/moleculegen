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
    
    private int counter;
    
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
        System.out.println("counter = " + counter);
    }
    
    public void run(IAtomContainer initial) {
        augment(new BondAugmentation(initial));
    }

    private void augment(Augmentation<IAtomContainer, BondExtension> parent) {
        counter++;
        if (canonicalChecker.isCanonical(parent)) {
            System.out.println(counter + " C " + io.AtomContainerPrinter.toString(parent.getBase()) + " -> " + parent.getExtension());
            if (augmentor.isComplete(parent)) {
                IAtomContainer atomContainer = parent.getBase();
                if (hCountValidator.isValidMol(atomContainer, atomContainer.getAtomCount())) {
                    handler.handle(atomContainer);
//                    System.out.println(counter + " " + io.AtomContainerPrinter.toString(atomContainer));
                    System.out.println("SOLUTION -------- " + io.AtomContainerPrinter.toString(atomContainer));
//                  System.out.println(io.AtomContainerPrinter.toString(atomContainer));
                }
            }
        } else {
            System.out.println(counter + " N " + io.AtomContainerPrinter.toString(parent.getBase()) + " -> " + parent.getExtension());
            return;
        }

        for (Augmentation<IAtomContainer, BondExtension> augmentation : augmentor.augment(parent)) {
            augment(augmentation);
        }
    }
}
