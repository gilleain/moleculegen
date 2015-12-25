package validate;

import java.util.Collections;
import java.util.List;

import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.atomtype.IAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import appbranch.FormulaParser;
import generate.BaseChildLister;

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
    
    private IAtomTypeMatcher matcher;
    
    public HCountValidator(String formulaString) {
        FormulaParser formulaParser = new FormulaParser(formulaString);
        hCount = formulaParser.getHydrogenCount();
        this.setSymbols(formulaParser.getElementSymbols());
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        matcher = CDKAtomTypeMatcher.getInstance(builder);
		hAdder = CDKHydrogenAdder.getInstance(builder);
		satCheck = new SaturationChecker();
    }
    
    public boolean isConnected(IAtomContainer atomContainer) {
        Object connectedProperty = atomContainer.getProperty("IS_CONNECTED");
        if (connectedProperty == null) {
            return true; // assume connected
        } else {
            if ((Boolean) connectedProperty) {
                return true;
            } else {
                boolean connected = ConnectivityChecker.isConnected(atomContainer);
                if (connected) {
                    atomContainer.setProperty("IS_CONNECTED", true);    
                } else {
                    atomContainer.setProperty("IS_CONNECTED", false);
                }
                return connected;
            }
        }
    }
    
    public void checkConnectivity(IAtomContainer atomContainer) {
        isConnected(atomContainer);
    }

    public boolean isValidMol(IAtomContainer atomContainer, int size) {
//        System.out.print("validating " + test.AtomContainerPrinter.toString(atomContainer));
        boolean valid = atomContainer.getAtomCount() == size
            && isConnected(atomContainer)
            && hydrogensCorrect(atomContainer)
//            && omgHCorrect(atomContainer);
            ;
//        System.out.println(valid);
        return valid;
    }
    
    private CDKHydrogenAdder hAdder;
	private SaturationChecker satCheck;
    
    private boolean omgHCorrect(IAtomContainer acprotonate) {
		try {
			for (IAtom atom : acprotonate.atoms()) {
				AtomTypeManipulator.configure(atom, matcher.findMatchingAtomType(acprotonate, atom));
			}
			hAdder.addImplicitHydrogens(acprotonate);
			if (AtomContainerManipulator.getTotalHydrogenCount(acprotonate) == hCount) {
				return satCheck.isSaturated(acprotonate);
			}
		} catch (CDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
    }

    private boolean hydrogensCorrect(IAtomContainer atomContainer) {
        try {
            int actualCount = 0;
            for (IAtom atom : atomContainer.atoms()) {
                IAtomType atomType = matcher.findMatchingAtomType(atomContainer, atom);
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
        	Integer hCount = atom.getImplicitHydrogenCount(); 
            implH += (hCount == null)? 0 : hCount;
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

    private void setSymbols(List<String> elementSymbols) {
        Collections.sort(elementSymbols);
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
