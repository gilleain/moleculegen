package validate;

import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;

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
        try {
            CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(atomContainer.getBuilder());
            int actualCount = 0;
            for (IAtom atom : atomContainer.atoms()) {
                IAtomType atomType = matcher.findMatchingAtomType(atomContainer, atom);
                if (atomType != null) {
                    actualCount += 
                            atomType.getFormalNeighbourCount() - 
                            atomContainer.getConnectedAtomsCount(atom);
                }
                if (actualCount > hCount) {
                    return false;
                }
            }

//            System.out.println("count for " + AtomContainerPrinter.toString(atomContainer) + " = " + actualCount);
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
