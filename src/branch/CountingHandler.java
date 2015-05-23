package branch;

import org.openscience.cdk.interfaces.IAtomContainer;

public class CountingHandler implements Handler {
    
    private int counter;
    
    @Override
    public void handle(IAtomContainer atomContainer) {
        counter++;
    }
    
    public int getCount() {
        return counter;
    }

    @Override
    public void finish() {
        // no op
    }

}
