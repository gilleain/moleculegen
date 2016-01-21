package augment.bond;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import app.FormulaParser;

public class ElementConstraints implements Iterable<String> {
    
    private Map<String, Integer> elementCounts;
    
    public ElementConstraints(String elementFormula) {
        this.elementCounts = new HashMap<String, Integer>();
        FormulaParser parser = new FormulaParser(elementFormula);
        for (String elementSymbol : parser.getElementSymbols()) {
            if (elementCounts.containsKey(elementSymbol)) {
                elementCounts.put(elementSymbol, elementCounts.get(elementSymbol) + 1);
            } else {
                elementCounts.put(elementSymbol, 1);
            }
        }
    }
    
    public ElementConstraints(Map<String, Integer> elementCounts, String toRemove) {
        this.elementCounts = new HashMap<String, Integer>();
        for (String element : elementCounts.keySet()) {
            int count;
            if (element.equals(toRemove)) {
                count = elementCounts.get(element) - 1;
            } else {
                count = elementCounts.get(element);
            }
            if (count > 0) {
                this.elementCounts.put(element, count);
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return elementCounts.keySet().iterator();
    }
    
    public ElementConstraints minus(String literal) {
        return new ElementConstraints(elementCounts, literal);
    }
    
    public String toString() {
        return this.elementCounts.toString();
    }
}
