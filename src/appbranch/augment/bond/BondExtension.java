package appbranch.augment.bond;

/**
 * Represents a single added bond.
 * 
 * @author maclean
 *
 */
public class BondExtension {

    private final IndexPair indexPair;
    
    private final ElementPair elementPair;

    public BondExtension(IndexPair indexPair, ElementPair elementPair) {
        this.indexPair = indexPair;
        this.elementPair = elementPair;
    }

    public IndexPair getIndexPair() {
        return indexPair;
    }

    public ElementPair getElementPair() {
        return elementPair;
    }

}
