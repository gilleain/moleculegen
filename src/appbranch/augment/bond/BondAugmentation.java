package appbranch.augment.bond;

import org.openscience.cdk.interfaces.IAtomContainer;

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

    public BondAugmentation(IAtomContainer augmentedMolecule, BondExtension bondExtension) {
        this.augmentedMolecule = augmentedMolecule;
        this.bondExtension = bondExtension;
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
