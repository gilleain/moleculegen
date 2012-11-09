package canonical;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface CanonicalLabeller {
    
    public String getCanonicalStringForm(IAtomContainer atomContainer);
    
    public int[] getLabels(IAtomContainer atomContainer);

}
