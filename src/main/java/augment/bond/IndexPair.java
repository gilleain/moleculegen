package augment.bond;

/**
 * The position to put a bond.
 * 
 * @author maclean
 *
 */
public class IndexPair {
    
    private final int start;
    
    private final int end;
    
    private final int order;

    public IndexPair(int start, int end, int order) {
        this.start = start;
        this.end = end;
        this.order = order;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
    
    public String toString() {
        return start + ":" + end + "(" + order + ")";
    }

    public int getOrder() {
        return order;
    }
}
