package augment.atom;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import augment.AugmentingGenerator;
import augment.chem.HCountValidator;
import augment.constraints.ElementConstraintSource;
import augment.constraints.ElementConstraints;
import handler.CanonicalHandler;
import handler.Handler;
import util.molecule.CDKUtilities;


public class AtomGenerator implements AugmentingGenerator<IAtomContainer> {

    public static final boolean SHOW_INTERMEDIATE_DATA = false; // add debugging code
    
    private static final long serialVersionUID = -5861481514308795440L;

    private AtomAugmentor augmentor;
    
    private Handler<IAtomContainer> handler;
    
    private int maxIndex;
    
    private HCountValidator hCountValidator;
    
    private AtomCanonicalChecker canonicalChecker;
    
    private ElementConstraints initialConstraints;
    
    private ElementConstraintSource initialStateSource;
    
    private CanonicalHandler<IAtomContainer> canonicalHandler;
    
    private int counter;
    
    public AtomGenerator(String elementFormula, Handler<IAtomContainer> handler) {
        // XXX - parse the formula once and pass down the parser!
        this.initialConstraints = new ElementConstraints(elementFormula);
        
        this.hCountValidator = new HCountValidator(elementFormula);
        initialStateSource = new ElementConstraintSource(initialConstraints);
        this.augmentor = new AtomAugmentor(hCountValidator.getElementSymbols());
        this.canonicalChecker = new AtomCanonicalChecker();
        this.handler = handler;
        this.maxIndex = hCountValidator.getElementSymbols().size() - 1;
    }
    
    public void setCanonicalHandler(CanonicalHandler<IAtomContainer> canonicalHandler) {
        this.canonicalHandler = canonicalHandler;
     }
    
    public void run() {
        for (IAtomContainer start : initialStateSource.get()) {
            String symbol = start.getAtom(0).getSymbol();
            augment(new AtomAugmentation(start, initialConstraints.minus(symbol)), 0);
        }
        // System.out.println("counter = " + counter);
    }
    
    public void run(IAtomContainer initial) {
        List<String> elements = new ArrayList<String>();
        for (int index = 0; index < initial.getAtomCount(); index++) {
            elements.add(initial.getAtom(index).getSymbol());
        }
        ElementConstraints remaining = new ElementConstraints(initialConstraints, new ElementConstraints(elements));
        augment(new AtomAugmentation(initial, remaining), initial.getAtomCount() - 1);  
    }
    
    private void augment(AtomAugmentation parent, int index) {
        
        if (!hCountValidator.canExtend(parent.getAugmentedObject())) return;
        
        counter++;
        if (index >= maxIndex) {
            IAtomContainer atomContainer = parent.getAugmentedObject();
            if(SHOW_INTERMEDIATE_DATA)
                System.out.println(CDKUtilities.atomContainerToString(atomContainer));
            if (hCountValidator.isValidMol(atomContainer, maxIndex + 1)) {
                handler.handle(atomContainer);
                if(SHOW_INTERMEDIATE_DATA)
                    System.out.println("OK " + CDKUtilities.atomContainerToString(atomContainer));

//                System.out.println("SOLN " + io.AtomContainerPrinter.toString(atomContainer));
            }
            return;
        }
        
        for (AtomAugmentation augmentation : augmentor.augment(parent)) {
            if (canonicalChecker.isCanonical(augmentation)) {
                report(true, parent, augmentation);
                augment(augmentation, index + 1);
            } else {
                report(false, parent, augmentation);
            }
        }
    }
    
    private void report(boolean isCanonical, AtomAugmentation parentAugmentation, AtomAugmentation childAugmentation) {
        if (canonicalHandler != null) {
            canonicalHandler.handle(
                    parentAugmentation.getAugmentedObject(), 
                    childAugmentation.getAugmentedObject(), 
                    isCanonical);
        }
    }

    @Override
    public void finish() {
        handler.finish();
    }

    @Override
    public Handler<IAtomContainer> getHandler() {
        return handler;
    }
}
