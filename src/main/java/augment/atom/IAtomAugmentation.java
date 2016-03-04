package augment.atom;

import augment.Augmentation;
import augment.constraints.ElementConstraints;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * augment.atom.IAtomAugmentation
 * User: Steve
 * Date: 2/22/2016
 */
public interface IAtomAugmentation extends Augmentation<IAtomContainer> {

    public IAtomContainer getAugmentedObject();

    public ElementConstraints getConstraints();

    public AtomExtension getExtension();
}
