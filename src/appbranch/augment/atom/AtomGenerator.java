package appbranch.augment.atom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import appbranch.augment.Augmentation;
import appbranch.augment.AugmentingGenerator;
import appbranch.canonical.CanonicalChecker;
import appbranch.canonical.NonExpandingCanonicalChecker;
import appbranch.handler.Handler;
import validate.HCountValidator;


public class AtomGenerator implements AugmentingGenerator {
    
    private AtomAugmentor augmentor;
    
    private Handler handler;
    
    private int maxIndex;
    
    private int hMax;
    
    private HCountValidator hCountValidator;
    
    private CanonicalChecker<IAtomContainer> canonicalChecker;
    
    public AtomGenerator(String elementFormula, Handler handler) {
        List<String> elementSymbols = new ArrayList<String>();
        this.hMax = parseFormula(elementFormula, elementSymbols);
        this.hCountValidator = new HCountValidator();
        hCountValidator.setHCount(hMax);
        hCountValidator.setElementSymbols(elementSymbols);
        
        this.augmentor = new AtomAugmentor(elementSymbols);
        this.canonicalChecker = new NonExpandingCanonicalChecker();
        this.handler = handler;
        this.maxIndex = elementSymbols.size() - 1;
    }
    
    private int parseFormula(String elementFormula, List<String> elementSymbols) {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IMolecularFormula formula = 
            MolecularFormulaManipulator.getMolecularFormula(elementFormula, builder);
        List<IElement> elements = MolecularFormulaManipulator.elements(formula);
        
        // count the number of non-heavy atoms
        int hCount = 0;
        for (IElement element : elements) {
            String symbol = element.getSymbol();
            int count = MolecularFormulaManipulator.getElementCount(formula, element);
            if (symbol.equals("H")) {
                hCount = count;
            } else {
                for (int i = 0; i < count; i++) {
                    elementSymbols.add(symbol);
                }
            }
        }
        Collections.sort(elementSymbols);
        
        return hCount;
    }
    
    public void run() {
        augment(augmentor.getInitial(), 0);
    }
    
    public void run(IAtomContainer initial) {
        // XXX index = atomCount?
        augment(new AtomAugmentation(initial), initial.getAtomCount() - 1);  
    }
    
    private void augment(Augmentation<IAtomContainer> parent, int index) {
        if (index >= maxIndex) {
            IAtomContainer atomContainer = parent.getAugmentedMolecule();
            if (hCountValidator.isValidMol(atomContainer, maxIndex + 1)) {
                handler.handle(atomContainer);
            }
            return;
        }
        
        for (Augmentation<IAtomContainer> augmentation : augmentor.augment(parent)) {
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
