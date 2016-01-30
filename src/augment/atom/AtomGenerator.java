package augment.atom;

import org.openscience.cdk.interfaces.IAtomContainer;

import augment.AugmentingGenerator;
import handler.Handler;
import validate.HCountValidator;


public class AtomGenerator implements AugmentingGenerator {
    
    private AtomAugmentor augmentor;
    
    private Handler handler;
    
    private int maxIndex;
    
    private HCountValidator hCountValidator;
    
    private AtomCanonicalChecker canonicalChecker;
    
    private ElementConstraints initialConstraints;
    
    private ElementConstraintSource initialStateSource;
    
    private int counter;
    
    public AtomGenerator(String elementFormula, Handler handler) {
        // XXX - parse the formula once and pass down the parser!
        this.initialConstraints = new ElementConstraints(elementFormula);
        
        this.hCountValidator = new HCountValidator(elementFormula);
        initialStateSource = new ElementConstraintSource(initialConstraints);
        this.augmentor = new AtomAugmentor(hCountValidator.getElementSymbols());
        this.canonicalChecker = new AtomCanonicalChecker();
        this.handler = handler;
        this.maxIndex = hCountValidator.getElementSymbols().size() - 1;
    }
    
    public void run() {
        for (IAtomContainer start : initialStateSource.get()) {
            String symbol = start.getAtom(0).getSymbol();
            augment(new AtomAugmentation(start, initialConstraints.minus(symbol)), 0);
        }
//        System.out.println("counter = " + counter);
    }
    
    public void run(IAtomContainer initial) {
        // XXX index = atomCount?
        ElementConstraints remaining = null;    // TODO
        augment(new AtomAugmentation(initial, remaining), initial.getAtomCount() - 1);  
    }
    
    private void augment(AtomAugmentation parent, int index) {
        
        counter++;
        if (index >= maxIndex) {
            IAtomContainer atomContainer = parent.getBase();
            if (hCountValidator.isValidMol(atomContainer, maxIndex + 1)) {
                handler.handle(atomContainer);
//                System.out.println("SOLN " + io.AtomContainerPrinter.toString(atomContainer));
            }
            return;
        }
        
        for (AtomAugmentation augmentation : augmentor.augment(parent)) {
            if (canonicalChecker.isCanonical(augmentation.getBase(), augmentation.getExtension())) {
//                report("C", augmentation);
                augment(augmentation, index + 1);
            } else {
//                report("N", augmentation);
            }
        }
    }
    
    private void report(String cOrN, AtomAugmentation augmentation) {
        System.out.println(counter + " " + cOrN + " " 
            + io.AtomContainerPrinter.toString(augmentation.getBase()));
    }

    @Override
    public void finish() {
        handler.finish();
    }
}
