package appbranch.augment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;

import appbranch.canonical.NautyLikeCanonicalChecker;
import group.AtomDiscretePartitionRefiner;
import group.BondDiscretePartitionRefiner;
import group.Permutation;
import group.PermutationGroup;
import io.AtomContainerPrinter;
import setorbit.BruteForcer;
import setorbit.SetOrbit;

/**
 * An {@link Augmentation} of an atom container by atom.
 * 
 * @author maclean
 *
 */
public class AtomAugmentation implements Augmentation<IAtomContainer> {
    
    private IAtomContainer augmentedMolecule;
    
    /**
     * An array the size of the parent, of bond orders to add
     */
    private int[] augmentation;
    
    /**
     * Construct the initial state.
     * 
     * @param initialAtom
     */
    public AtomAugmentation(IAtom initialAtom) {
        augmentedMolecule = initialAtom.getBuilder().newInstance(IAtomContainer.class);
        augmentedMolecule.addAtom(initialAtom);
        augmentedMolecule.setProperty("IS_CONNECTED", false);
    }
    
    public AtomAugmentation(IAtomContainer initialContainer) {
        augmentedMolecule = initialContainer;   // TODO : could clone...
    }
    
    /**
     * Make an augmentation from a parent, an atom, and a set of bonds. The augmentation
     * array is a list like {0, 1, 0, 2} which means add a single bond to atom 1 and 
     * a double to atom 3, connecting both to the new atom. 
     *  
     * @param parent the atom container to augment
     * @param atomToAdd the additional atom
     * @param augmentation a list of bond orders to augment
     */
    public AtomAugmentation(IAtomContainer parent, IAtom atomToAdd, int[] augmentation) {
        this.augmentation = augmentation;
        this.augmentedMolecule = make(parent, atomToAdd, augmentation);
    }
    
    public IAtomContainer getAugmentedMolecule() {
        return augmentedMolecule;
    }

    private IAtomContainer make(IAtomContainer parent, IAtom atomToAdd, int[] augmentation) {
        try {
            int lastIndex = augmentation.length;
            IAtomContainer child = (IAtomContainer) parent.clone();
            child.addAtom(atomToAdd);

            for (int index = 0; index < augmentation.length; index++) {
                int value = augmentation[index];
                if (value > 0) {
                    Order order;
                    switch (value) {
                        case 1: order = Order.SINGLE; break;
                        case 2: order = Order.DOUBLE; break;
                        case 3: order = Order.TRIPLE; break;
                        default: order = Order.SINGLE;
                    }
                    child.addBond(index, lastIndex, order);
                    IAtom partner = child.getAtom(index);
                    Integer hCount = partner.getImplicitHydrogenCount();
                    int partnerCount = (hCount == null)? 0 : hCount;
                    partner.setImplicitHydrogenCount(partnerCount - value);
                }
            }
            return child;
        } catch (CloneNotSupportedException cnse) {
            // TODO
            return null;
        }
    }
}
