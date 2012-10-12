package handler;

import org.openscience.cdk.interfaces.IAtomContainer;

public class CountingHandler implements GenerateHandler {
    
    private int count;
    
    public CountingHandler() {
        count = 0;
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
        System.out.println(count + " structures");
    }

}
