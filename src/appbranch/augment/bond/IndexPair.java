package appbranch.augment.bond;

/**
 * The position to put a bond.
 * 
 * @author maclean
 *
 */
public class IndexPair {
    
    private final int start;
    
    private final int end;

    public IndexPair(int start, int end) {
        super();
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
