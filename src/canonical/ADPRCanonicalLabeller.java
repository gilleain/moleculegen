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
        refiner.reset();
        String stringForm = AtomContainerPrinter.toString(atomContainer, labelling, true);
        return stringForm;
    }

    @Override
    public int[] getLabels(IAtomContainer atomContainer) {
        refiner.getAutomorphismGroup(atomContainer);
        int[] labels = refiner.getBest().invert().getValues();
        refiner.reset();
        return labels;
    }

}
