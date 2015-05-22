package branch;

import org.openscience.cdk.interfaces.IAtomContainer;


public class AtomGenerator {
    
    private AtomAugmentor augmentor;
    
    private Handler handler;
    
    private int maxIndex;
    
    public AtomGenerator(String elementSymbols, Handler handler, int maxSize) {
        this.augmentor = new AtomAugmentor(elementSymbols);
        this.handler = handler;
        this.maxIndex = maxSize - 1;
    }
    
    public void run() {
        augment(augmentor.getInitial(), 1);
    }
    
    public void run(IAtomContainer initial) {
        augment(new AtomAugmentation(initial), 1);
    }
    
    private void augment(Augmentation<IAtomContainer> parent, int index) {
        if (index >= maxIndex) {
            handler.handle(parent.getAugmentedMolecule());
            return;
        }
        
        for (Augmentation<IAtomContainer> augmentation : augmentor.augment(parent)) {
            if (augmentation.isCanonical()) {
                augment(augmentation, index + 1);
            }
        }
    }
}
