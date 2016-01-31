package graphhandler;

import model.Graph;

/**
 * 
 * TODO : make this generic, then have a set of molecule ones...
 * 
 * @author maclean
 *
 */
public interface GraphHandler {
    
    public void handle(Graph g);
    
    public void finish();

}
