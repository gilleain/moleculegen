package generate;

import io.AtomContainerPrinter;

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
        this(new SignatureCanonicalLabeller());
    }
    
    public AtomFilteringChildLister(CanonicalLabeller labeller) {
        super();
        this.labeller = labeller;
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
    
    public AtomFilteringChildLister(List<String> elementSymbols, CanonicalLabeller labeller) {
        this(labeller);
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
//            System.out.println(
//                    AtomContainerPrinter.toString(parent)
//                    + " + "
//                    + java.util.Arrays.toString(bondOrderArray)
//                    + " -> "
//                    + AtomContainerPrinter.toString(child)
//                    );
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
