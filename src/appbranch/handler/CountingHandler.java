package appbranch.handler;

import org.openscience.cdk.interfaces.IAtomContainer;

import appbranch.Handler;

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
