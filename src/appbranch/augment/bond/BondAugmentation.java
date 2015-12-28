package appbranch.augment.bond;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import appbranch.augment.Augmentation;

/**
 * Augmentation of an atom container by adding a bond between two atoms, or 
 * incrementing the bond order if there is already a bond.
 *  
 * @author maclean
 *
 */
public class BondAugmentation implements Augmentation<IAtomContainer, BondExtension> {
    
    private final IAtomContainer augmentedMolecule;
    
    private final BondExtension bondExtension;
    
    private final IChemObjectBuilder builder;

    public BondAugmentation(IAtomContainer parent) {
        this.augmentedMolecule = parent; // TODO : could clone...
        this.bondExtension = null;  // XXX
        this.builder = SilentChemObjectBuilder.getInstance();
    }
    
    public BondAugmentation(IAtomContainer parent, BondExtension bondExtension) {
        this.bondExtension = bondExtension;
        this.builder = SilentChemObjectBuilder.getInstance();
        this.augmentedMolecule = extend(parent, bondExtension);
    }
    
    private IAtomContainer extend(IAtomContainer parent, BondExtension bondExtension) {
        try {
            IAtomContainer child = (IAtomContainer) parent.clone();
            IndexPair position = bondExtension.getIndexPair();
            if (position.getEnd() > parent.getAtomCount() - 1) {
                String elementSymbol = bondExtension.getElementPair().getEndSymbol();
                child.addAtom(builder.newInstance(IAtom.class, elementSymbol));
                child.addBond(position.getStart(), position.getEnd(), IBond.Order.SINGLE);
            } else {
                // oh how I wish there was a hasBond method...
                IBond bond = child.getBond(
                        child.getAtom(position.getStart()), child.getAtom(position.getEnd()));
                if (bond == null) {
                    child.addBond(position.getStart(), position.getEnd(), IBond.Order.SINGLE);
                } else {
                    bond.setOrder(incrementOrder(bond.getOrder()));
                }
            }
            return child;
        } catch (CloneNotSupportedException cnse) {
            // TODO
            return null;
        }
    }
    
    private IBond.Order incrementOrder(IBond.Order order) {
        switch (order) {
            case SINGLE: return IBond.Order.DOUBLE;
            case DOUBLE: return IBond.Order.TRIPLE;
            case TRIPLE: return null;   // XXX
            default: return null;
        }
    }
    
    @Override
    public IAtomContainer getBase() {
        return augmentedMolecule;
    }

    @Override
    public BondExtension getExtension() {
        return bondExtension;
    }
}
