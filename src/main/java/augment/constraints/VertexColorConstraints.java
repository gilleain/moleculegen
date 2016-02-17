package augment.constraints;

import java.util.ArrayList;
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
    
    public VertexColorConstraints(Map<String, Integer> elementCounts, String... toRemove) {
        this.colorCounts = new HashMap<String, Integer>();
        List<String> toRemoveCandidates = new ArrayList<String>();
        for (String toRemoveCandidate : toRemove) {
            toRemoveCandidates.add(toRemoveCandidate);
        }
        for (String element : elementCounts.keySet()) {
            int count;
            if (remove(element, toRemoveCandidates)) {
                count = elementCounts.get(element) - 1;
            } else {
                count = elementCounts.get(element);
            }
            if (count > 0) {
                this.colorCounts.put(element, count);
            }
        }
    }
    
    private boolean remove(String element, List<String> toRemoveCandidates) {
        if (toRemoveCandidates.contains(element))  {
            toRemoveCandidates.remove(element);
            return true;
        } else {
            return false;
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
