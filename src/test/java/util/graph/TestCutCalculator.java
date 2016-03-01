package util.graph;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import model.Graph;

public class TestCutCalculator {
    
    
    private void printBonds(List<Integer> edgeIndices, Graph g) {
        for (int edgeIndex : edgeIndices) {
            System.out.println(g.getEdge(edgeIndex));
        }
    }
    
    private void testEdges(String gString, int expectedCount) {
        Graph g = new Graph(gString); 
        List<Integer> indices = CutCalculator.getCutEdges(g);
        printBonds(indices, g);
        assertEquals(expectedCount, indices.size());
    }
    
    private void testVertices(String gString, int expectedCount) {
        Graph g = new Graph(gString); 
        List<Integer> indices = CutCalculator.getCutVertices(g);
        System.out.println(indices);
        assertEquals(expectedCount, indices.size());
    }
    
    @Test
    public void testTree() {
        testEdges("C0C1C2C3C4C5C6 0:1(1),1:2(1),1:3(1),3:5(1),4:5(1),5:6(1)", 2);
    }
    
    @Test
    public void bridgedRings() {
        testEdges("C0C1C2C3C4C5C6 0:1(1),0:2(1),1:2(1),1:3(1),3:4(1),3:5(1),4:6(1),5:6(1)", 1);
    }
    
    @Test
    public void spiraRings() {
        testVertices("C0C1C2C3C4C5C6 0:1(1),0:3(1),1:2(1),2:3(1),3:4(1),3:6(1),4:5(1),5:6(1)", 1);
    }
    
    @Test
    public void testCutVerticesInSingleRing() {
        testVertices("C0C1N2C3 0:1(3),0:2(1),1:2(1),2:3(1)", 1);
    }
    
    @Test
    public void testWonkyBowtie() {
        testEdges("C0C1C2C3C4C5C6 0:1(1),0:2(1),1:3(1),2:4(1),3:5(1),0:4(1),1:5(1),5:6(1)", 1);
    }

}
