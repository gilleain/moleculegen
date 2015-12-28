package appbranch.augment.bond;

/**
 * The pair of element symbols at the end points of an
 * extension.
 * 
 * @author maclean
 *
 */
public class ElementPair {
    
    private final String startSymbol;
    
    private final String endSymbol;

    public ElementPair(String startSymbol, String endSymbol) {
        this.startSymbol = startSymbol;
        this.endSymbol = endSymbol;
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public String getEndSymbol() {
        return endSymbol;
    }
    
    public String toString() {
        return "(" + startSymbol + "," + endSymbol + ")";
    }
}
