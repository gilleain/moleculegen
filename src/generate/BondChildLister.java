package generate;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.signature.MoleculeSignature;


public class BondChildLister extends BaseChildLister {
    
    private static final IBond.Order[] orders = { 
        IBond.Order.SINGLE,
        IBond.Order.DOUBLE,
        IBond.Order.TRIPLE
    };
   
    public List<IAtomContainer> listChildren(IAtomContainer parent, int size) {
        int n = parent.getAtomCount();
        List<IAtomContainer> children = new ArrayList<IAtomContainer>();
        List<String> certificates = new ArrayList<String>();
        int[] satCap = getSaturationCapacity(parent);
        int max = Math.min(n + 1, size);
        for (IBond.Order order : orders) {
            int o = orderToInt(order);
            for (int i = 0; i < n; i++) {
                if (satCap[i] < o) continue;
                if (!orderValid(i, o)) continue;
                for (int j = i + 1; j < max; j++) {
                    if (j < n && satCap[j] < o) continue;
                    if (!orderValid(j, o)) continue;
                    if (parent.getBond(parent.getAtom(i), parent.getAtom(j)) != null) continue;
                    IAtomContainer child = makeChild(parent, i, j, order);
                    MoleculeSignature molSig = new MoleculeSignature(child);
                    String cert = molSig.toCanonicalString();
                    if (!certificates.contains(cert)) {
                        certificates.add(cert);
                        children.add(child);
//                        System.out.println(test.AtomContainerPrinter.toString(child));
                    }
                }
            }
        }
        return children;
    }
    
    private int orderToInt(IBond.Order order) {
        switch (order) {
            case SINGLE: return 1;
            case DOUBLE: return 2;
            case TRIPLE: return 3;
            default:     return 0;
        }
    }
    
    private boolean orderValid(int atomIndex, int order) { 
        int maxBondOrder = super.getMaxBondOrder(atomIndex);
        return maxBondOrder >= order;
    }
    
    public IAtomContainer makeChild(
            IAtomContainer parent, int atomIndexI, int atomIndexJ, IBond.Order order) {
        try {
            IAtomContainer child = (IAtomContainer) parent.clone();
            if (atomIndexJ >= parent.getAtomCount()) {
                String atomSymbol = getElementSymbols().get(atomIndexJ);
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
        if (atomContainer.getAtomCount() < size) {
            return false;
        } else {
            int[] satCap = getSaturationCapacity(atomContainer);
            int freeCount = 0;
            for (int i = 0; i < satCap.length; i++) {
                if (satCap[i] > 0) freeCount++;
            }
            return freeCount < 2;
        }
    }

}
