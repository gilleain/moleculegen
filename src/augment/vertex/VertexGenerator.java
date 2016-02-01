package augment.vertex;

import augment.AugmentingGenerator;
import augment.constraints.VertexColorConstraintSource;
import augment.constraints.VertexColorConstraints;
import handler.Handler;
import model.Graph;
import validate.HCountValidator;


/**
 * Generator for graphs that augments by a vertex and a set of edges.
 * 
 * @author maclean
 *
 */
public class VertexGenerator implements AugmentingGenerator<Graph> {
    
    private VertexAugmentor augmentor;
    
    private Handler<Graph> handler;
    
    private int maxIndex;
    
    private HCountValidator hCountValidator;
    
    private VertexCanonicalChecker canonicalChecker;
    
    private VertexColorConstraints initialConstraints;
    
    private VertexColorConstraintSource initialStateSource;
    
    private int counter;
    
    public VertexGenerator(String elementFormula, Handler<Graph> handler) {
        // XXX - parse the formula once and pass down the parser!
        this.initialConstraints = new VertexColorConstraints(elementFormula);
        
        this.hCountValidator = new HCountValidator(elementFormula);
        initialStateSource = new VertexColorConstraintSource(initialConstraints);
        this.augmentor = new VertexAugmentor(hCountValidator.getElementSymbols());
        this.canonicalChecker = new VertexCanonicalChecker();
        this.handler = handler;
        this.maxIndex = hCountValidator.getElementSymbols().size() - 1;
    }
    
    public void run() {
        for (Graph start : initialStateSource.get()) {
            String symbol = start.getVertexColor(0);
            augment(new ByVertexAugmentation(start, initialConstraints.minus(symbol)), 0);
        }
//        System.out.println("counter = " + counter);
    }
    
    public void run(Graph initial) {
        // XXX index = atomCount?
        VertexColorConstraints remaining = null;    // TODO
        augment(new ByVertexAugmentation(initial, remaining), initial.getVertexCount() - 1);  
    }
    
    private void augment(ByVertexAugmentation parent, int index) {
        counter++;
        if (index >= maxIndex) {
            Graph graph = parent.getAugmentedObject();
            handler.handle(graph);
//            System.out.println("SOLN " + graph);
            return;
        }
        
        for (ByVertexAugmentation augmentation : augmentor.augment(parent)) {
            if (canonicalChecker.isCanonical(augmentation)) {
//                report("C", augmentation);
                augment(augmentation, index + 1);
            } else {
//                report("N", augmentation);
            }
        }
    }
    
    private void report(String cOrN, ByVertexAugmentation augmentation) {
        System.out.println(counter + " " + cOrN + " " + augmentation.getAugmentedObject());
    }

    @Override
    public void finish() {
        handler.finish();
    }
}
