package augment.bond;

/**
 * Represents a single added bond.
 * 
 * @author maclean
 *
 */
public class BondExtension {

    private final IndexPair indexPair;
    
    private final String elementSymbol;

    public BondExtension(IndexPair indexPair, String elementSymbol) {
        this.indexPair = indexPair;
        this.elementSymbol = elementSymbol;
    }

    public IndexPair getIndexPair() {
        return indexPair;
    }

    public String getElementSymbol() {
        return elementSymbol;
    }
    
    public String toString() {
        return indexPair + ", " + (elementSymbol == null? "{ }" : elementSymbol);
    }

}
