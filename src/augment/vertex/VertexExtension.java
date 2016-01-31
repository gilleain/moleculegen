package augment.vertex;

/**
 * The atom and bond information to add when augmenting.
 * 
 * @author maclean
 *
 */
public class VertexExtension {
    
    private final String elementSymbol;
    
    private final int[] bondOrderList;

    public VertexExtension(String elementSymbol, int[] bondOrderList) {
        this.elementSymbol = elementSymbol;
        this.bondOrderList = bondOrderList;
    }

    public String getElementSymbol() {
        return elementSymbol;
    }

    public int[] getBondOrderList() {
        return bondOrderList;
    }

}
