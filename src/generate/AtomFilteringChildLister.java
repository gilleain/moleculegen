package generate;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import canonical.CanonicalLabeller;
import canonical.SignatureCanonicalLabeller;

/**
 * List candidate children of a parent molecule, by connecting a new atom to the existing atoms 
 * through a set of bonds that are minimal in their orbit.
 * 
 * @author maclean
 *
 */
public class AtomFilteringChildLister extends BaseAtomChildLister implements ChildLister {
    
    private CanonicalLabeller labeller;
    
    public AtomFilteringChildLister() {
        super();
        labeller = new SignatureCanonicalLabeller();
    }
    
    /**
     * Convenience constructor for testing.
     * 
     * @param elementString
     */
    public AtomFilteringChildLister(List<String> elementSymbols) {
        this();
        setElementSymbols(elementSymbols);
    }
    
    public List<IAtomContainer> listChildren(IAtomContainer parent, int currentAtomIndex) {
        int maxDegreeSumForCurrent = getMaxBondOrderSum(currentAtomIndex);
        int maxDegreeForCurrent = getMaxBondOrder(currentAtomIndex);
        List<IAtomContainer> children = new ArrayList<IAtomContainer>();
        List<String> certs = new ArrayList<String>();
        
        for (int[] bondOrderArray : getBondOrderArrays(
                parent, currentAtomIndex, maxDegreeSumForCurrent, maxDegreeForCurrent)) {
            IAtomContainer child = makeChild(parent, bondOrderArray, currentAtomIndex);
            String certificate = labeller.getCanonicalStringForm(child);
            if (certs.contains(certificate)) {
                continue;
            } else {
                children.add(child);
                certs.add(certificate);
            }
        }
        
        children.add(makeDisconnectedChild(parent, currentAtomIndex));
        
        return children;
    }

}
