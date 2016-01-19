package appbranch.augment.atom;

import org.openscience.cdk.interfaces.IAtomContainer;

import appbranch.FormulaParser;
import appbranch.Generator;
import appbranch.augment.Augmentation;
import appbranch.augment.AugmentingGenerator;
import appbranch.augment.ExtensionSource;
import appbranch.augment.InitialStateSource;
import appbranch.canonical.CanonicalChecker;
import appbranch.canonical.NonExpandingCanonicalChecker;
import appbranch.handler.Handler;
import validate.HCountValidator;


public class AtomGenerator implements AugmentingGenerator, Generator {
    
    private AtomAugmentor augmentor;
    
    private Handler handler;
    
    private int maxIndex;
    
    private HCountValidator hCountValidator;
    
    private CanonicalChecker<IAtomContainer, AtomExtension> canonicalChecker;
    
    private InitialStateSource<IAtomContainer> initialStateSource;
    
    private int counter;
    
    public AtomGenerator(String elementFormula, Handler handler) {
        // XXX - make these once and pass them down!
        FormulaParser formulaParser = new FormulaParser(elementFormula);
        
        this.hCountValidator = new HCountValidator(elementFormula);
        initialStateSource = new AtomOnlyStart(elementFormula);
        ExtensionSource<Integer, String> extensionSource = new ElementSymbolSource(formulaParser);
        this.augmentor = new AtomAugmentor(hCountValidator.getElementSymbols(), extensionSource);
        this.canonicalChecker = new NonExpandingCanonicalChecker();
        this.handler = handler;
        this.maxIndex = hCountValidator.getElementSymbols().size() - 1;
    }
    
    public void run() {
        for (IAtomContainer start : initialStateSource.get()) {
            augment(new AtomAugmentation(start), 0);
        }
//        System.out.println("counter = " + counter);
    }
    
    public void run(IAtomContainer initial) {
        // XXX index = atomCount?
        augment(new AtomAugmentation(initial), initial.getAtomCount() - 1);  
    }
    
    private void augment(Augmentation<IAtomContainer, AtomExtension> parent, int index) {
        counter++;
        if (index >= maxIndex) {
            IAtomContainer atomContainer = parent.getBase();
            if (hCountValidator.isValidMol(atomContainer, maxIndex + 1)) {
                handler.handle(atomContainer);
//                System.out.println(io.AtomContainerPrinter.toString(atomContainer));
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
