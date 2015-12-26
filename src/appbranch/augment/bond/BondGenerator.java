package appbranch.augment.bond;

import org.openscience.cdk.interfaces.IAtomContainer;

import appbranch.augment.Augmentation;
import appbranch.augment.InitialStateSource;
import appbranch.canonical.CanonicalChecker;
import appbranch.handler.Handler;

public class BondGenerator {
    
    private Handler handler;
    
    private BondAugmentor augmentor;
    
    private InitialStateSource<IAtomContainer> initialStateSource;
    
    private CanonicalChecker<IAtomContainer, BondExtension> canonicalChecker;
    
    public BondGenerator(String elementFormula, Handler handler) {
        this.handler = handler;
        this.augmentor = new BondAugmentor(elementFormula);
    }
    
    public void run() {
        for (IAtomContainer startingStructure : initialStateSource.get()) {
            augment(new BondAugmentation(startingStructure, null));   // XXX null Bond Extension
        }
    }

    private void augment(Augmentation<IAtomContainer, BondExtension> parent) {
        // TODO Auto-generated method stub
        
        for (Augmentation<IAtomContainer, BondExtension> augmentation : augmentor.augment(parent)) {
            if (canonicalChecker.isCanonical(augmentation)) {
                augment(augmentation);
            }
        }
    }
}
