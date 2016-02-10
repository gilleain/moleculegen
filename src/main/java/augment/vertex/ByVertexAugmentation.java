package augment.vertex;

import augment.Augmentation;
import augment.constraints.VertexColorConstraints;
import model.Graph;

/**
 * An augmentation of an atom container by atom.
 * 
 * @author maclean
 *
 */
public class ByVertexAugmentation implements Augmentation<Graph> {
    
    private final Graph augmentedGraph;
    
    private final VertexExtension vertexExtension;
    
    private final VertexColorConstraints elementConstraints;
    
   
    /**
     * Construct the initial state.
     * 
     * @param initialAtom
     */
    public ByVertexAugmentation(String initialVertexColor, VertexColorConstraints elementConstraints) {
        augmentedGraph = new Graph();
        augmentedGraph.addVertex(initialVertexColor);
        
        // XXX TODO - wrong way round!
        this.vertexExtension = new VertexExtension(initialVertexColor, new int[] {});
        this.elementConstraints = elementConstraints;
    }
    
    public ByVertexAugmentation(Graph initialGraph, VertexColorConstraints elementConstraints) {
        augmentedGraph = new Graph(initialGraph);
        this.vertexExtension = null;  // XXX!
        this.elementConstraints = elementConstraints;
    }
    
    /**
     * Make an augmentation from a parent, an atom, and a set of bonds. The augmentation
     * array is a list like {0, 1, 0, 2} which means add a single bond to atom 1 and 
     * a double to atom 3, connecting both to the new atom. 
     *  
     * @param parent the atom container to augment
     * @param atomToAdd the additional atom
     * @param augmentation a list of bond orders to augment
     */
    public ByVertexAugmentation(Graph parent, String atomToAdd, int[] bondOrders, VertexColorConstraints elementConstraints) {
        this.vertexExtension = new VertexExtension(atomToAdd, bondOrders);
        this.augmentedGraph = make(parent, atomToAdd, bondOrders);
        this.elementConstraints = elementConstraints;
    }
    
    public Graph getAugmentedObject() {
        return augmentedGraph;
    }
    
    public VertexColorConstraints getConstraints() {
        return elementConstraints;
    }

    private Graph make(Graph parent, String atomToAdd, int[] augmentation) {
        int lastIndex = augmentation.length;
        Graph child = new Graph(parent);
        child.addVertex(atomToAdd);

        for (int index = 0; index < augmentation.length; index++) {
            int value = augmentation[index];
            if (value > 0) {
                child.addEdge(index, lastIndex, value);
            }
        }
        return child;
    }

    public VertexExtension getExtension() {
        return vertexExtension;
    }
}
