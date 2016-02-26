package augment.constraints;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import app.FormulaParser;

public class VertexColorConstraints implements Iterable<String> {
    
    private Map<String, Integer> colorCounts;
    
    public VertexColorConstraints(String elementFormula) {
        this.colorCounts = new HashMap<String, Integer>();
        FormulaParser parser = new FormulaParser(elementFormula);
        for (String elementSymbol : parser.getElementSymbols()) {
            if (colorCounts.containsKey(elementSymbol)) {
                colorCounts.put(elementSymbol, colorCounts.get(elementSymbol) + 1);
            } else {
                colorCounts.put(elementSymbol, 1);
            }
        }
    }
    
    public VertexColorConstraints(List<String> elements) {
        this.colorCounts = new HashMap<String, Integer>();
        for (String elementSymbol : elements) {
            if (colorCounts.containsKey(elementSymbol)) {
                colorCounts.put(elementSymbol, colorCounts.get(elementSymbol) + 1);
            } else {
                colorCounts.put(elementSymbol, 1);
            }
        }
    }
    
    public VertexColorConstraints(Map<String, Integer> elementCounts, String toRemove) {
        this.colorCounts = new HashMap<String, Integer>();
        for (String element : elementCounts.keySet()) {
            int count;
            if (element.equals(toRemove)) {
                count = elementCounts.get(element) - 1;
            } else {
                count = elementCounts.get(element);
            }
            if (count > 0) {
                this.colorCounts.put(element, count);
            }
        }
    }
    
    public VertexColorConstraints(VertexColorConstraints source, VertexColorConstraints diff) {
        this.colorCounts = new HashMap<String, Integer>();
        for (String element : source.colorCounts.keySet()) {
            int count;
            if (diff.colorCounts.containsKey(element)) {
                count = source.colorCounts.get(element) - diff.colorCounts.get(element);
                diff.colorCounts.remove(element);
            } else {
                count = source.colorCounts.get(element);
            }
            if (count > 0) {
                this.colorCounts.put(element, count);
            }
        }
    }
    
    public Map<String, Integer> getMap() {
        return this.colorCounts;
    }

    @Override
    public Iterator<String> iterator() {
        return colorCounts.keySet().iterator();
    }
    
    public VertexColorConstraints minus(String literal) {
        return new VertexColorConstraints(colorCounts, literal);
    }
    
    public String toString() {
        return this.colorCounts.toString();
    }
}
