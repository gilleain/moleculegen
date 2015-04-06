package branch;

import org.openscience.cdk.interfaces.IAtomContainer;

public class AtomGenerator {
    
    private AtomAugmentor augmentor;
    
    private int maxSize;
    
    public AtomGenerator(String elementSymbols, int maxSize) {
        augmentor = new AtomAugmentor(elementSymbols);
        this.maxSize = maxSize;
    }
    
    public void run() {
        augment(getInitialContainer(), 1);
    }
    
    private void augment(IAtomContainer atomContainer, int index) {
        if (index >= maxSize) return;
        
        for (Augmentation augmentation : augmentor.augment(atomContainer)) {
            if (augmentation.isCanonical()) {
                augment(augmentation.getAugmentedMolecule(), index++);
            }
        }
    }
    
    private IAtomContainer getInitialContainer() {
        return null;    // XXX
    }

}
