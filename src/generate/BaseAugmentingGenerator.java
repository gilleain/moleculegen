package generate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

public abstract class BaseAugmentingGenerator {
    
    public abstract void setHCount(int hCount);
    
    public abstract void setElementSymbols(List<String> elementSymbol);
    
    public abstract List<String> getElementSymbols();
    
    public int getHeavyAtomCount() {
    	return getElementSymbols() == null? 0 : getElementSymbols().size();
    }
    
    /**
     * Set the hydrogen count and the heavy atom symbol string from the formula, 
     * returning the count of heavy atoms.
     * 
     * @param formulaString
     * @param generator
     * @return
     */
    public int setParamsFromFormula(String formulaString) {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IMolecularFormula formula = 
            MolecularFormulaManipulator.getMolecularFormula(formulaString, builder);
        List<IElement> elements = MolecularFormulaManipulator.elements(formula);
        
        // count the number of non-heavy atoms
        int hCount = 0;
        List<String> elementSymbols = new ArrayList<String>();
        for (IElement element : elements) {
            String symbol = element.getSymbol();
            int count = MolecularFormulaManipulator.getElementCount(formula, element);
            if (symbol.equals("H")) {
                hCount = count;
            } else {
                for (int i = 0; i < count; i++) {
                    elementSymbols.add(symbol);
                }
            }
        }
        setHCount(hCount);
        Collections.sort(elementSymbols);
        setElementSymbols(elementSymbols);
        
        return elementSymbols.size();
    }

}
