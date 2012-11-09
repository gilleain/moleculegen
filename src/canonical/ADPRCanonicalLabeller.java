package canonical;

import group.AtomDiscretePartitionRefiner;
import group.Permutation;
import io.AtomContainerPrinter;

import org.openscience.cdk.interfaces.IAtomContainer;

public class ADPRCanonicalLabeller implements CanonicalLabeller {
    
    private AtomDiscretePartitionRefiner refiner;
    
    public ADPRCanonicalLabeller() {
        refiner = new AtomDiscretePartitionRefiner();
    }

    @Override
    public String getCanonicalStringForm(IAtomContainer atomContainer) {
        refiner.getAutomorphismGroup(atomContainer);
        Permutation labelling = refiner.getBest().invert();
        return AtomContainerPrinter.toString(atomContainer, labelling);
    }

    @Override
    public int[] getLabels(IAtomContainer atomContainer) {
        refiner.getAutomorphismGroup(atomContainer);
        return refiner.getBest().invert().getValues();
    }

}
