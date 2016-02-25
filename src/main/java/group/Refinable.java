package group;

/**
 * Objects that are refinable by the equitable and discrete partition refiners.
 * 
 * @author maclean
 *
 */
public interface Refinable {
    
    int[] getConnectedIndices(int vertexIndex);

    int getVertexCount();

    int getConnectivity(int vertexI, int vertexJ);
    
    int getMaxConnectivity();

}
