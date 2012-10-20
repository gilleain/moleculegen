package generate;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.signature.MoleculeSignature;


public class BondChildLister extends BaseChildLister {
    
    private List<String> elementSymbols;
    
    private static final IBond.Order[] orders = { 
        IBond.Order.SINGLE,
        IBond.Order.DOUBLE,
        IBond.Order.TRIPLE
    };
   
    public List<IAtomContainer> listChildren(IAtomContainer parent) {
        int n = parent.getAtomCount();
        List<IAtomContainer> children = new ArrayList<IAtomContainer>();
        List<String> certificates = new ArrayList<String>();
        int[] satCap = getSaturationCapacity(parent);
        for (IBond.Order order : orders) {
            for (int i = 0; i < n; i++) {
                if (satCap[i] <= 0) continue;
                for (int j = i + 1; j <= n; j++) {
                    if (satCap[j] <= 0) continue;
                    IAtomContainer child = makeChild(parent, i, j, order);
                    MoleculeSignature molSig = new MoleculeSignature(child);
                    String cert = molSig.toCanonicalString();
                    if (!certificates.contains(cert)) {
                        certificates.add(cert);
                        children.add(child);
                    }
                }
            }
        }
        return children;
    }
    
    public IAtomContainer makeChild(
            IAtomContainer parent, int atomIndexI, int atomIndexJ, IBond.Order order) {
        try {
            IAtomContainer child = (IAtomContainer) parent.clone();
            if (atomIndexJ >= parent.getAtomCount()) {
                String atomSymbol = elementSymbols.get(atomIndexJ);
                child.addAtom(child.getBuilder().newInstance(IAtom.class, atomSymbol));
            }
            
            child.addBond(atomIndexI, atomIndexJ, order);
//            System.out.println(java.util.Arrays.toString(bondOrderArr) + "\t" 
//                    + test.AtomContainerPrinter.toString(child));
            return child;
        } catch (CloneNotSupportedException cnse) {
            // TODO
            return null;
        }
    }
    
    public boolean isFinished(IAtomContainer atomContainer, int size) {
        if (atomContainer.getAtomCount() < size) return true;
        int[] satCap = getSaturationCapacity(atomContainer);
        int freeCount = 0;
        for (int i = 0; i < satCap.length; i++) {
            if (satCap[i] > 0) freeCount++;
        }
        return freeCount > 2;
    }

}
