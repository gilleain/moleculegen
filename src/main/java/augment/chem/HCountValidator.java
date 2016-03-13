package augment.chem;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.atomtype.IAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.FastChemObjectBuilder;

import app.FormulaParser;

/**
 * Validate a molecule as having the correct number of hydrogens.
 * 
 * @author maclean
 */
public class HCountValidator implements Serializable {
    
    private static final long serialVersionUID = -1097301781318130707L;

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
    
    private final BondOrderMaps bondOrderMaps;
    
    public HCountValidator() {
        this.bondOrderMaps = new BondOrderMaps();
    }
    
    public HCountValidator(FormulaParser formulaParser) {
        this();
        hCount = formulaParser.getHydrogenCount();
        this.setSymbols(formulaParser.getElementSymbols());
    }

    
    protected int[] getSaturationCapacity(IAtomContainer parent) {
        int[] satCap = new int[parent.getAtomCount()];
        for (int index = 0; index < parent.getAtomCount(); index++) {
            IAtom atom = parent.getAtom(index);
            int maxDegree = bondOrderMaps.getMaxBondOrderSum(atom.getSymbol());
            int degree = 0;
            for (IBond bond : parent.getConnectedBondsList(atom)) {
                degree += bond.getOrder().ordinal() + 1;
            }
            satCap[index] = maxDegree - degree;
        }
        return satCap;
    }
    
    public static int HydrogensIncorrectCount = 0;
    public static int CountIncorrectCount = 0;
    public static int MoleculeCorrectCount = 0;

    public boolean isValidMol(IAtomContainer atomContainer, int size) {
        boolean b = hydrogensCorrect(atomContainer);
        if (!b) {
            HydrogensIncorrectCount++;
            return false;
        }
        boolean b1 = atomContainer.getAtomCount() == size;
        if (!b1) {
            CountIncorrectCount++;
            return false;
        }
        MoleculeCorrectCount++;
        return true;
    }

    private IAtomTypeMatcher getMatcher() {
        IChemObjectBuilder builder =  FastChemObjectBuilder.getInstance();   // changed SLewis for control
        return CDKAtomTypeMatcher.getInstance(builder);
    }
    
    public boolean hydrogensCorrect(IAtomContainer atomContainer) {
        try {
            IAtomTypeMatcher matcher = getMatcher();
            int actualCount = 0;
          //  System.out.println("=========================");
            for (IAtom atom : atomContainer.atoms()) {
                IAtomType atomType = null;
                try {
                    atomType = matcher.findMatchingAtomType(atomContainer, atom);
           //         System.out.println(CDKUtilities.atomContainerToString(atomContainer) + "=>" + CDKUtilities.atomToString(atomType));
                } catch (CDKException pE) {
                    atomType = matcher.findMatchingAtomType(atomContainer, atom);
                    throw new RuntimeException(pE);
                }
                if (atomType == null || atomType.getAtomTypeName().equals("X")) {
//                	System.out.println(
//                			"+ 0 NULL " 
//                					+ " for atom " + atomContainer.getAtomNumber(atom)
//                					+ " in " +  AtomContainerPrinter.toString(atomContainer)
//                			);
                } else {
                	int count = 
                			atomType.getFormalNeighbourCount() - 
                			atomContainer.getConnectedAtomsCount(atom); 
//                	System.out.println(
//                			"+" + count
//                			+ " " + atomType.getAtomTypeName() 
//                			+ " for atom " + atomContainer.getAtomNumber(atom)
//                			+ " in " +  AtomContainerPrinter.toString(atomContainer)
//                			);
                	actualCount += count;
//                    actualCount += atom.getImplicitHydrogenCount();
                }
                if (actualCount > hCount) {
                    return false;
                }
            }

            return actualCount == hCount;
        } catch (CDKException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

            // TODO Auto-generated catch block
            //     return false;
        }
    }


    public boolean canExtend(IAtomContainer atomContainer) {
        int implH = 0;
        for (IAtom atom : atomContainer.atoms()) {
//        	Integer hCount = atom.getImplicitHydrogenCount(); 
//            implH += (hCount == null)? 0 : hCount;
            int atomHCount = bondOrderMaps.getMaxBondOrderSum(atom.getSymbol());
            for (IBond bond : atomContainer.getConnectedBondsList(atom)) {
                atomHCount -= bond.getOrder().numeric();
            }
            implH += atomHCount;
        }
        int index = atomContainer.getAtomCount() - 1;
        int hAdd = maxHAdd[index];
        int hRem = maxHRem[index];
        int min = implH - hRem;
        int max = implH + hAdd;
        boolean extensible = (min <= hCount && hCount <= max); 
        String acp = io.AtomContainerPrinter.toString(atomContainer);
//        System.out.println(
//                    hCount
//                    + "\t" +  implH 
//                    + "\t" + hAdd
//                    + "\t" + hRem
//                    + "\t" + min 
//                    + "\t" + max
//                    + "\t" + extensible
//                    + "\t" + acp);
//        return true;
        return extensible;
    }

    public void setImplicitHydrogens(IAtomContainer parent) {
        for (IAtom atom : parent.atoms()) {
            int maxBos = bondOrderMaps.getMaxBondOrderSum(atom.getSymbol());
            int neighbourCount = parent.getConnectedAtomsCount(atom);
            atom.setImplicitHydrogenCount(maxBos - neighbourCount);
        }
    }

    private void setSymbols(List<String> elementSymbols) {
        Collections.sort(elementSymbols);
        maxBOS = 0;
        int size = elementSymbols.size();
        int[] bosList = new int[size];
        for (int index = 0; index < elementSymbols.size(); index++) {
            String elementSymbol = elementSymbols.get(index);
            maxBOS += bondOrderMaps.getMaxBondOrderSum(elementSymbol);
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
