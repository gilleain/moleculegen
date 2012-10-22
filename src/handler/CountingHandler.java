package handler;

import org.openscience.cdk.interfaces.IAtomContainer;

public class CountingHandler implements GenerateHandler {
    
    private int count;
    
    private boolean isTiming;
    
    private long startTime;
    
    public CountingHandler() {
        this(false);
    }

    public CountingHandler(boolean isTiming) {
        count = 0;
        this.isTiming = isTiming; 
        if (isTiming) {
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void handle(IAtomContainer parent, IAtomContainer child) {
        count++;
    }
    
    public int getCount() {
        return count;
    }

    @Override
    public void finish() {
        if (isTiming) {
            long time = System.currentTimeMillis() - startTime;
            System.out.println(count + " structures in " + time + " ms");
        } else {
            System.out.println(count + " structures");
        }
    }

}
