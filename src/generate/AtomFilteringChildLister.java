package generate;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;

/**
 * List candidate children of a parent molecule, by connecting a new atom to the existing atoms 
 * through a set of bonds that are minimal in their orbit.
 * 
 * @author maclean
 *
 */
public class AtomFilteringChildLister extends BaseAtomChildLister implements SignatureChildLister {
    
    /**
     * An optimisation : since the signature of the parent has to be calculated to
     * make the group, we calculate it here to be used in the validator for the 
     * canonical checking step 
     */
    private String parentSignature;
    
    public AtomFilteringChildLister() {
        super();
    }
    
    /**
     * Convenience constructor for testing.
     * 
     * @param elementString
     */
    public AtomFilteringChildLister(String elementString) {
        this();
        setElementString(elementString);
    }
    
    public String getParentSignature() {
        return parentSignature;
    }
    
    public List<IAtomContainer> listChildren(IAtomContainer parent, int currentAtomIndex) {
        int maxDegreeSumForCurrent = getMaxBondOrderSum(currentAtomIndex);
        int maxDegreeForCurrent = getMaxBondOrder(currentAtomIndex);
        List<IAtomContainer> children = new ArrayList<IAtomContainer>();
        List<String> certs = new ArrayList<String>();
        int maxMultisetSize = Math.min(currentAtomIndex, maxDegreeSumForCurrent);
        parentSignature = new MoleculeSignature(parent).toCanonicalString();
        for (int[] bondOrderArray : getBondOrderArrays(parent, maxMultisetSize, maxDegreeForCurrent)) {
            IAtomContainer child = makeChild(parent, bondOrderArray, currentAtomIndex);
            MoleculeSignature molSig = new MoleculeSignature(child);
            String molSigString = molSig.toCanonicalString();
            if (certs.contains(molSigString)) {
                continue;
            } else {
                children.add(child);
                certs.add(molSigString);
            }
        }
        return children;
    }
}
