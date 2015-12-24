package appbranch.augment.bond;

import org.openscience.cdk.interfaces.IAtomContainer;

import appbranch.augment.Augmentation;
import appbranch.augment.StateSource;
import appbranch.canonical.CanonicalChecker;
import appbranch.handler.Handler;

public class BondGenerator {
    
    private Handler handler;
    
    private BondAugmentor augmentor;
    
    private StateSource<IAtomContainer, BondExtension> stateSource;
    
    private CanonicalChecker<IAtomContainer, BondExtension> canonicalChecker;
    
    public void run() {
        for (IAtomContainer startingStructure : stateSource.get()) {
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
