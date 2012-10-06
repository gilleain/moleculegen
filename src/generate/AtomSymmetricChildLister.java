package generate;

import group.SSPermutationGroup;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond.Order;

import combinatorics.MultiKSubsetLister;

public class AtomSymmetricChildLister implements ChildLister {
    
    public List<IAtomContainer> listChildren(IAtomContainer parent, int currentAtomIndex) {
        int maxDegreeForCurrent = getMaxDegree(parent.getAtom(currentAtomIndex));
        SSPermutationGroup autG = getGroup(parent);
        List<IAtomContainer> children = new ArrayList<IAtomContainer>();
        for (List<Integer> multiset : getMultisets(parent, maxDegreeForCurrent)) {
            if (isMinimal(multiset, autG)) {
                children.add(makeChild(parent, multiset, currentAtomIndex));
            }
        }
        return children;
    }
    
    private IAtomContainer makeChild(
            IAtomContainer parent, List<Integer> multiset, int lastIndex) {
        try {
            IAtomContainer child = (IAtomContainer) parent.clone();
            for (int value : multiset) {
                if (value > 0) {
                    Order order;
                    switch (value) {
                        case 1: order = Order.SINGLE;
                        case 2: order = Order.DOUBLE;
                        case 3: order = Order.TRIPLE;
                        default: order = Order.SINGLE;
                    }
                    child.addBond(value, lastIndex, order);
                }
            }
            return child;
        } catch (CloneNotSupportedException cnse) {
            // TODO
            return null;
        }
    }
    
    private boolean isMinimal(List<Integer> multiset, SSPermutationGroup autG) {
        // TODO Auto-generated method stub
        return false;
    }

    private List<List<Integer>> getMultisets(IAtomContainer parent, int maxDegreeForCurrent) {
        // these are the atom indices that can be added to
        List<Integer> baseSet = new ArrayList<Integer>();
        MultiKSubsetLister<Integer> lister = new MultiKSubsetLister<Integer>(maxDegreeForCurrent, baseSet);
        // TODO Auto-generated method stub
        return null;
    }

    private SSPermutationGroup getGroup(IAtomContainer parent) {
        // TODO Auto-generated method stub
        return null;
    }

    private int getMaxDegree(IAtom atom) {
        // TODO Auto-generated method stub
        return 0;
    }

}
