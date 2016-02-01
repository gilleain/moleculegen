package handler.graph;

import handler.Handler;
import model.Graph;

public class GraphCountingHandler implements Handler<Graph> {
    
    private int count;
    
    private boolean isTiming;
    
    private long startTime;
    
    public GraphCountingHandler(boolean isTiming) {
        count = 0;
        this.isTiming = isTiming; 
        if (isTiming) {
            startTime = System.currentTimeMillis();
        }
    }
    
    @Override
    public void handle(Graph graph) {
        count++;
    }
    
    public int getCount() {
        return count;
    }

    @Override
    public void finish() {
        if (isTiming) {
            long time = System.currentTimeMillis() - startTime;
            System.out.println(count + " structures in " + time + " ms");
        } else {
            System.out.println(count + " structures");
        }
    }
    

}
