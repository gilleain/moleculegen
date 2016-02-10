package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * Utility class to parse a formula string (like "C2H8NO2") into the element symbols 
 * for the heavy elements (like {"C", "C", "N", "O", "O"}) and the hydrogen count (8).
 * 
 * @author maclean
 *
 */
public class FormulaParser {
    
    /**
     * Sorted list of element symbols, expanded to the number of atoms.
     */
    private final List<String> elementSymbols;
    
    /**
     * The hydrogen count for the formula.
     */
    private final int hCount;
    
    public FormulaParser(String formulaString) {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IMolecularFormula formula = 
            MolecularFormulaManipulator.getMolecularFormula(formulaString, builder);
        List<IElement> elements = MolecularFormulaManipulator.elements(formula);
        
        // accumulate heavy atoms
        elementSymbols = new ArrayList<String>();
        int counter = 0;
        for (IElement element : elements) {
            String symbol = element.getSymbol();
            int count = MolecularFormulaManipulator.getElementCount(formula, element);
            if (symbol.equals("H")) {
                counter += count;
            } else {
                for (int i = 0; i < count; i++) {
                    elementSymbols.add(symbol);
                }
            }
        }
        hCount = counter;
        Collections.sort(elementSymbols);
    }
    
    public int getHydrogenCount() {
        return hCount;
    }
    
    public List<String> getElementSymbols() {
        return elementSymbols;
    }

}
