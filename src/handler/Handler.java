package handler;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface Handler {
    
    public void handle(IAtomContainer atomContainer);
    
    public void finish();

}
