package branch;


public class AtomGenerator {
    
    private AtomAugmentor augmentor;
    
    private int maxSize;
    
    public AtomGenerator(String elementSymbols, int maxSize) {
        augmentor = new AtomAugmentor(elementSymbols);
        this.maxSize = maxSize;
    }
    
    public void run() {
        augment(getInitial(), 1);
    }
    
    private void augment(Augmentation parent, int index) {
        if (index >= maxSize) return;
        
        for (Augmentation augmentation : augmentor.augment(parent)) {
            if (augmentation.isCanonical()) {
                augment(augmentation, index++);
            }
        }
    }
    
    private Augmentation getInitial() {
        return null;    // XXX
    }

}
