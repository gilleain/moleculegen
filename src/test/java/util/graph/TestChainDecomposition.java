package util.graph;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import model.Edge;
import model.Graph;

public class TestChainDecomposition {
    
    private void printBridges(Graph g, ChainDecomposition chainDecomposition) {
        for (Edge edge : chainDecomposition.getBridges()) {
            System.out.println(edge);
        }
    }
    
    private void printCycles(Graph g, ChainDecomposition chainDecomposition) {
        int count = 1;
        for (List<Edge> cycle : chainDecomposition.getCycleChains()) {
            System.out.print(count + " ");
            for (Edge edge : cycle) {
                System.out.print(edge + ",");
            }
            System.out.println();
            count++;
        }
    }
    
    @Test
    public void spira() {
        Graph g = new Graph("C0C1C2C3C4C5C6 0:1(1),0:3(1),1:2(1),2:3(1),3:4(1),3:6(1),4:5(1),5:6(1)");
        ChainDecomposition chainDecomposition = new ChainDecomposition(g);
        assertEquals(0, chainDecomposition.getBridges().size());
        printCycles(g, chainDecomposition);
    }
    
    @Test
    public void bridgedRings() {
        Graph g = new Graph("C0C1C2C3C4C5C6 0:1(1),0:2(1),1:2(1),1:3(1),3:4(1),3:5(1),4:6(1),5:6(1)");
        ChainDecomposition chainDecomposition = new ChainDecomposition(g);
        assertEquals(1, chainDecomposition.getBridges().size());
        printCycles(g, chainDecomposition);
        printBridges(g, chainDecomposition);
    }
    
    @Test
    public void fourLine() {
        Graph g = new Graph("C0C1C2C3 0:1(1),1:2(1),2:3(1)");
        ChainDecomposition chainDecomposition = new ChainDecomposition(g);
        printBridges(g, chainDecomposition);
    }

}
