package augment.bond;

import org.openscience.cdk.interfaces.IAtomContainer;

import augment.AugmentingGenerator;
import augment.ConstrainedAugmentation;
import handler.Handler;
import validate.HCountValidator;

public class BondGenerator implements AugmentingGenerator {
    
    private ElementConstraints initialConstraints;
    
    private Handler handler;
    
    private BondAugmentor augmentor;

    private BondCanonicalChecker canonicalChecker;
    
    private ElementConstraintSource initialStateSource;
    
    private HCountValidator hCountValidator;
    
    private int counter;
    
    public BondGenerator(String elementFormula, Handler handler) {
        this.initialConstraints = new ElementConstraints(elementFormula); 
        this.augmentor = new BondAugmentor(elementFormula);
        this.handler = handler;
        canonicalChecker = new BondCanonicalChecker();
        initialStateSource = new ElementConstraintSource(initialConstraints);
        hCountValidator = new HCountValidator(elementFormula);
    }
    
    public void run() {
        for (IAtomContainer startingStructure : initialStateSource.get()) {
            // XXX null Bond Extension
            String symbol = startingStructure.getAtom(0).getSymbol();
            run(startingStructure, initialConstraints.minus(symbol));
        }
//        System.out.println("counter = " + counter);
    }
    
    @Override
    public void run(IAtomContainer initial) {
        // TODO Auto-generated method stub
        
    }

    public void run(IAtomContainer initial, ElementConstraints constraints) {
        augment(new BondAugmentation(initial, constraints));
    }
    
    private void augment(
            ConstrainedAugmentation<IAtomContainer, BondExtension, ElementConstraints> parent) {
        counter++;
        IAtomContainer atomContainer = parent.getBase();
        if (canonicalChecker.isCanonical(atomContainer, parent.getExtension())) {
//            System.out.println(counter + " C " + toString(parent));
            if (augmentor.isComplete(atomContainer)) {
                if (hCountValidator.isValidMol(atomContainer, atomContainer.getAtomCount())) {
//                    System.out.println("SOL "+ io.AtomContainerPrinter.toString(atomContainer));
                    handler.handle(atomContainer);
                }
            }
        } else {
//            System.out.println(counter + " N " + toString(parent));
            return;
        }
        
        for (ConstrainedAugmentation<
                IAtomContainer, BondExtension, ElementConstraints> augmentation : augmentor.augment(parent)) {
            augment(augmentation);
        }
    }
    
    private String toString(ConstrainedAugmentation<IAtomContainer, BondExtension, ElementConstraints> aug) {
        return io.AtomContainerPrinter.toString(aug.getBase()) + " -> " + aug.getExtension() + ", " + aug.getConstraints();
    }

    @Override
    public void finish() {
        handler.finish();
    }
}
