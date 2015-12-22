package appbranch.augment.bond;

/**
 * Represents a single added bond.
 * 
 * @author maclean
 *
 */
public class BondExtension {

    private final int startIndex;
    
    private final int endIndex;
    
    public BondExtension(int startIndex, int endIndex) {
        super();
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

}
