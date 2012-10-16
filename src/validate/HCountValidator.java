package validate;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Validate a molecule as having the correct number of hydrogens.
 * 
 * @author maclean
 *
 */
public class HCountValidator implements MoleculeValidator {
    
    private int hCount;
    
    public HCountValidator() {
        hCount = 0;
    }

    @Override
    public boolean isValidMol(IAtomContainer atomContainer, int size) {
        // TODO!
//        System.out.println("validating " + test.AtomContainerPrinter.toString(atomContainer));
        return atomContainer.getAtomCount() == size && hydrogensCorrect(atomContainer);
    }

    private boolean hydrogensCorrect(IAtomContainer atomContainer) {
//        if (hCount < 1) return true;    // XXX
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
            CDKHydrogenAdder adder = 
                CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
            adder.addImplicitHydrogens(atomContainer);
            int actualCount = 0;
            for (IAtom atom : atomContainer.atoms()) {
                actualCount += AtomContainerManipulator.countHydrogens(atomContainer, atom);
                if (actualCount > hCount) {
                    return false;
                }
            }
            return actualCount == hCount;
        } catch (CDKException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public void setHCount(int hCount) {
        this.hCount = hCount;
    }

}
