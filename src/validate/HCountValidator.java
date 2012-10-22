package validate;

import java.util.List;

import generate.BaseChildLister;

import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Validate a molecule as having the correct number of hydrogens.
 * 
 * XXX it extends base child lister to get access to the BOS stuff - not nice design...
 * 
 * @author maclean
 *
 */
public class HCountValidator extends BaseChildLister implements MoleculeValidator {
    
    private int hCount;
    
    private int maxBOS;
    
    /**
     * For each position, the maximum number of hydrogens that could be added.
     */
    private int[] maxHAdd;
    
    /**
     * For each position, the maximum number of hydrogens that could be removed.
     */
    private int[] maxHRem;
    
    private CDKAtomTypeMatcher matcher;
    
    public HCountValidator() {
        hCount = 0;
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        matcher = CDKAtomTypeMatcher.getInstance(builder);
    }

    @Override
    public boolean isValidMol(IAtomContainer atomContainer, int size) {
//        System.out.println("validating " + test.AtomContainerPrinter.toString(atomContainer));
        return atomContainer.getAtomCount() == size && hydrogensCorrect(atomContainer);
    }

    private boolean hydrogensCorrect(IAtomContainer atomContainer) {
        try {
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

    @Override
    public boolean canExtend(IAtomContainer atomContainer) {
        int implH = 0;
        for (IAtom atom : atomContainer.atoms()) {
            implH += atom.getImplicitHydrogenCount();
        }
        int index = atomContainer.getAtomCount() - 1;
        int hAdd = maxHAdd[index];
        int hRem = maxHRem[index];
        int min = implH - hRem;
        int max = implH + hAdd;
        boolean extensible = (min <= hCount && hCount <= max); 
//        String acp = test.AtomContainerPrinter.toString(atomContainer);
//        System.out.println(implH 
//                    + "\t" + hAdd
//                    + "\t" + hRem
//                    + "\t" + min 
//                    + "\t" + max
//                    + "\t" + extensible
//                    + "\t" + acp);
//        return true;
        return extensible;
    }

    @Override
    public void setImplicitHydrogens(IAtomContainer parent) {
        for (IAtom atom : parent.atoms()) {
            int maxBos = super.getMaxBondOrderSum(atom.getSymbol());
            int neighbourCount = parent.getConnectedAtomsCount(atom);
            atom.setImplicitHydrogenCount(maxBos - neighbourCount);
        }
    }

    @Override
    public void setElementSymbols(List<String> elementSymbols) {
        super.setElementSymbols(elementSymbols);
        maxBOS = 0;
        int size = elementSymbols.size();
        int[] bosList = new int[size];
        for (int index = 0; index < elementSymbols.size(); index++) {
            String elementSymbol = elementSymbols.get(index);
            maxBOS += super.getMaxBondOrderSum(elementSymbol);
            bosList[index] = maxBOS;
        }
        
        maxHAdd = new int[size];
        maxHRem = new int[size];
        for (int index = 0; index < elementSymbols.size(); index++) {
            // if all remaining atoms were added with max valence,
            // how many implicit hydrogens would be removed?
            maxHRem[index] = maxBOS - bosList[index]; 
            
            // if the minimum number of connections were made (a tree),
            // how many hydrogens could be added
            maxHAdd[index] = (maxBOS - bosList[index]) - (size - index);
        }
//        System.out.println("Add" + java.util.Arrays.toString(maxHAdd));
//        System.out.println("Rem" + java.util.Arrays.toString(maxHRem));
//        
//        System.out.println("ImplH" + "\t" + "hAdd" + "\t" + "hRem" + "\t"
//                          + "min" + "\t" + "max" + "\t" + "extend?" + "\t" + "acp");
    }
}
