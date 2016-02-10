package handler.graph;

import static org.openscience.cdk.interfaces.IBond.Order.DOUBLE;
import static org.openscience.cdk.interfaces.IBond.Order.SINGLE;
import static org.openscience.cdk.interfaces.IBond.Order.TRIPLE;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import handler.Handler;
import model.Edge;
import model.Graph;

/**
 * Converts graphs to molecules, then passes those on to the delegate handler.
 * 
 * @author maclean
 *
 */
public class MoleculeAdaptor implements Handler<Graph> {
    
    private IChemObjectBuilder builder;
    
    private Handler<IAtomContainer> delegate;
    
    public MoleculeAdaptor(Handler<IAtomContainer> delegate) {
        this(SilentChemObjectBuilder.getInstance(), delegate);
    }
    
    public MoleculeAdaptor(IChemObjectBuilder builder, Handler<IAtomContainer> delegate) {
        this.builder = builder;
        this.delegate = delegate;
    }

    @Override
    public void handle(Graph graph) {
        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        
        // add the atoms
        for (int vertexIndex = 0; vertexIndex < graph.getVertexCount(); vertexIndex++) {
            String vertexColor = graph.getVertexColor(vertexIndex);
            atomContainer.addAtom(builder.newInstance(IAtom.class, vertexColor));
        }
        
        // add the bonds
        for (Edge edge : graph.edges()) {
            IBond.Order order = getOrder(graph, edge); 
            atomContainer.addBond(edge.getVertex(0), edge.getVertex(1), order);
        }
        
        delegate.handle(atomContainer);
    }

    private Order getOrder(Graph graph, Edge edge) {
        int color = graph.getEdgeColor(edge.getVertex(0), edge.getVertex(1)); 
        switch (color) {
            case 1: return SINGLE; 
            case 2: return DOUBLE;
            case 3: return TRIPLE;
            default: return SINGLE;
        }
    }

    @Override
    public void finish() {
        delegate.finish();
    }

}
