package augment.constrained;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Augmentation of an atom container by adding a bond between two atoms, or 
 * incrementing the bond order if there is already a bond.
 *  
 * @author maclean
 *
 */
public class BondAugmentation implements ConstrainedAugmentation<IAtomContainer, BondExtension, ElementConstraints> {
    
    private final IAtomContainer augmentedMolecule;
    
    private final BondExtension bondExtension;
    
    private final IChemObjectBuilder builder;
    
    private final ElementConstraints elementConstraints;

    public BondAugmentation(IAtomContainer parent, ElementConstraints elementConstraints) {
        this.augmentedMolecule = parent; // TODO : could clone...
        this.bondExtension = null;  // XXX
        this.builder = SilentChemObjectBuilder.getInstance();
        this.elementConstraints = elementConstraints;
    }
    
    public BondAugmentation(IAtomContainer parent, BondExtension bondExtension, ElementConstraints elementConstraints) {
        this.bondExtension = bondExtension;
        this.builder = SilentChemObjectBuilder.getInstance();
        this.augmentedMolecule = extend(parent, bondExtension);
        this.elementConstraints = elementConstraints;
    }
    
    private IAtomContainer extend(IAtomContainer parent, BondExtension bondExtension) {
        try {
            IAtomContainer child = (IAtomContainer) parent.clone();
            IndexPair position = bondExtension.getIndexPair();
            if (position.getEnd() > parent.getAtomCount() - 1) {
                String elementSymbol = bondExtension.getElementSymbol();
                child.addAtom(builder.newInstance(IAtom.class, elementSymbol));
                child.addBond(position.getStart(), position.getEnd(), toBond(position.getOrder()));
            } else {
                // oh how I wish there was a hasBond method...
                IBond bond = child.getBond(
                        child.getAtom(position.getStart()), child.getAtom(position.getEnd()));
                if (bond == null) {
                    child.addBond(position.getStart(), position.getEnd(), toBond(position.getOrder()));
                } else {
                    // throw error?
                    System.err.println("Shouldn't have a bond here!");
                }
            }
            return child;
        } catch (CloneNotSupportedException cnse) {
            System.err.println(cnse);
            return null;
        }
    }
    
    private IBond.Order toBond(int order) {
        switch (order) {
            case 1: return IBond.Order.SINGLE;
            case 2: return IBond.Order.DOUBLE;
            case 3: return IBond.Order.TRIPLE;
            default: return IBond.Order.UNSET;  // XXX?
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
    
    public String toString() {
        return io.AtomContainerPrinter.toString(augmentedMolecule) + " -> " + bondExtension;
    }

    @Override
    public ElementConstraints getConstraints() {
        return elementConstraints;
    }
}
