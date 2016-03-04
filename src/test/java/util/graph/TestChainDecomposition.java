package util.graph;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import model.Edge;
import model.Graph;

public class TestChainDecomposition {
    
    private void printAll(ChainDecomposition chainDecomposition) {
        printBridges(chainDecomposition);
        printPaths(chainDecomposition);
        printCycles(chainDecomposition);
    }
    
    private void printBridges(ChainDecomposition chainDecomposition) {
        int count = 1;
        for (Edge edge : chainDecomposition.getBridges()) {
            System.out.println("Bridge " + count++ + " " + edge);
        }
    }
    
    private void printCycles(ChainDecomposition chainDecomposition) {
        printChains("Cycle", chainDecomposition.getCycleChains());
    }
    
    private void printPaths(ChainDecomposition chainDecomposition) {
        printChains("Path", chainDecomposition.getPathChains());
    }
    
    private void printChains(String prefix, List<List<Edge>> chains) {
        int count = 1;
        for (List<Edge> path : chains) {
            System.out.print(prefix + " " + count + " ");
            for (Edge edge : path) {
                System.out.print(edge + ",");
            }
            System.out.println();
            count++;
        }
    }
    
    private void bridgeCount(int count, ChainDecomposition chainDecomposition) {
        assertEquals("Bridges", count, chainDecomposition.getBridges().size());
    }
    
    private void cycleCount(int count, ChainDecomposition chainDecomposition) {
        assertEquals("Cycles", count, chainDecomposition.getCycleChains().size());
    }
    
    private void pathCount(int count, ChainDecomposition chainDecomposition) {
        assertEquals("Paths", count, chainDecomposition.getPathChains().size());
    }
    
    private ChainDecomposition get(String graphString) {
        return new ChainDecomposition(new Graph(graphString));
    }

    @Test
    public void spira() {
        ChainDecomposition chainDecomposition = 
          get("C0C1C2C3C4C5C6 0:1(1),0:3(1),1:2(1),2:3(1),3:4(1),3:6(1),4:5(1),5:6(1)");
        
        assertEquals(0, chainDecomposition.getBridges().size());
        printCycles(chainDecomposition);
    }
    
    @Test
    public void bridgedRings() {
        ChainDecomposition chainDecomposition = 
            get("C0C1C2C3C4C5C6 0:1(1),0:2(1),1:2(1),1:3(1),3:4(1),3:5(1),4:6(1),5:6(1)");
        bridgeCount(1, chainDecomposition);
        printCycles(chainDecomposition);
        printBridges(chainDecomposition);
    }
    
    @Test
    public void fourLine() {
        ChainDecomposition chainDecomposition = get("C0C1C2C3 0:1(1),1:2(1),2:3(1)");
        printBridges(chainDecomposition);
        printPaths(chainDecomposition);
    }
    
    @Test
    public void wonkyBowtie() {
        ChainDecomposition chainDecomposition = 
//        get("C0C1C2C3C4C5C6 0:1(1),0:2(1),1:3(1),2:4(1),3:5(1),0:4(1),1:5(1),5:6(1)");
        get("C0C1C2C3C4C5C6 0:1(1),0:2(1),1:2(1),1:3(1),3:4(1),3:5(1),4:5(1),5:6(1)");
        
        printAll(chainDecomposition);
        
        cycleCount(2, chainDecomposition);
        bridgeCount(2, chainDecomposition);
        pathCount(0, chainDecomposition);
    }
    
    @Test
    public void multiFused() {
        ChainDecomposition chainDecomposition = 
           get("C0C1C2C3C4C5C6 0:1(1),0:2(1),1:3(1),1:4(1),1:5(1),2:3(1),2:4(1),2:6(1),5:6(1)");
        printAll(chainDecomposition);
    }
    
    @Test
    public void bridgedArmedSquare() {
        ChainDecomposition chainDecomposition = 
                get("C0C1C2C3C4C5C6 0:1(1),0:2(1),0:3(1),1:4(1),4:5(1),2:4(1),0:5(1),3:6(1)");
        printAll(chainDecomposition);
    }

}
