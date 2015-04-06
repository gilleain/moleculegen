package branch;

import group.AtomDiscretePartitionRefiner;
import group.Permutation;
import group.PermutationGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;

import setorbit.BruteForcer;
import setorbit.SetOrbit;

/**
 * An {@link Augmentation} of an atom container by atom.
 * 
 * @author maclean
 *
 */
public class AtomAugmentation implements Augmentation {
    
    private IAtomContainer augmentedMolecule;
    
    /**
     * An array the size of the parent, of bond orders to add
     */
    private int[] augmentation;
    
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

    @Override
    public boolean isCanonical() {
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        PermutationGroup autH = refiner.getAutomorphismGroup(augmentedMolecule);
        Permutation labelling = refiner.getBest().invert();
        int[] connected = getConnected(augmentedMolecule, labelling);
        return inOrbit(connected, autH);
    }
    
    private boolean inOrbit(int[] targetSetArray, PermutationGroup autH) {
        List<Integer> targetSet = toSet(targetSetArray);
        List<Integer> augmentationSet = toSet(augmentation);
        System.out.println("target " + targetSet + " aug " + augmentationSet);
        
        SetOrbit orbit = new BruteForcer().getInOrbit(targetSet, autH);
        for (List<Integer> subset : orbit) {
            System.out.println("subset "  + subset);
            if (subset.equals(augmentationSet)) {
                return true;
            }
        }
        return false;
    }
    
    private List<Integer> toSet(int[] setArray) {
        List<Integer> set = new ArrayList<Integer>();
        for (int index = 0; index < setArray.length; index++) {
            int element = setArray[index];
            if (element > 0) {
                set.add(index);
            }
        }
        return set;
    }
    
    private int[] getConnected(IAtomContainer h, Permutation labelling) {
        int[] connected = new int[h.getAtomCount()];
        int chosen = labelling.get(h.getAtomCount() - 1);
        System.out.println("chosen " + chosen + " under labelling " + labelling);
        IAtom chosenAtom = h.getAtom(chosen);
        for (int bondIndex = 0; bondIndex < h.getBondCount(); bondIndex++) {
            IBond bond = h.getBond(bondIndex); 
            if (bond.contains(chosenAtom)) {
                IAtom partner = bond.getConnectedAtom(chosenAtom);
                connected[h.getAtomNumber(partner)] = bond.getOrder().numeric();
            }
        }
        System.out.println("connected " + Arrays.toString(connected));
        return connected;
    }

}
