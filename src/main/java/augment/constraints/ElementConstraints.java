package augment.constraints;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import app.FormulaParser;

public class ElementConstraints implements Iterable<String>, Serializable {
    private static final long serialVersionUID = 636691183652463986L;
    
    private Map<String, Integer> elementCounts;
    
    public ElementConstraints(String elementFormula) {
        this(new FormulaParser(elementFormula).getElementSymbols());
    }
    
    public ElementConstraints(List<String> elements) {
        this.elementCounts = new HashMap<String, Integer>();
        for (String elementSymbol : elements) {
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
    
    public ElementConstraints(ElementConstraints source, ElementConstraints diff) {
        this.elementCounts = new HashMap<String, Integer>();
        for (String element : source.elementCounts.keySet()) {
            int count;
            if (diff.elementCounts.containsKey(element)) {
                count = source.elementCounts.get(element) - diff.elementCounts.get(element);
                diff.elementCounts.remove(element);
            } else {
                count = source.elementCounts.get(element);
            }
            if (count > 0) {
                this.elementCounts.put(element, count);
            }
        }
    }
    
    public Map<String, Integer> getMap() {
        return this.elementCounts;
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

    public int getCount(String element) {
        return this.elementCounts.get(element);
    }
}
