package group;

import org.junit.Test;

import group.Permutation;
import group.PermutationGroup;
import group.graph.GraphDiscretePartitionRefiner;
import model.Graph;

public class GraphAutomorphismGroupTest {
    
    private void test(String graphString) {
        Graph graph = new Graph(graphString);
        GraphDiscretePartitionRefiner refiner = new GraphDiscretePartitionRefiner();
        PermutationGroup aut = refiner.getAutomorphismGroup(graph);
        System.out.println("G: " + graphString);
        System.out.println("AUT(G):");
        for (Permutation p : aut.all()) {
            System.out.println(p);
        }
    }
    
    @Test
    public void test() {
        test("C0C1C2C3C4C5 0:1(1),0:2(1),0:3(1),3:4(2),4:5(1),4:6(1),5:6(1)");
    }
    
    @Test
    public void testNineA() {
        test("C0C1C2C3C4C5C6C7C8 0:1(1),0:2(1),0:3(1),1:4(2),4:5(1),5:6(1),6:7(2),6:8(1)");
    }
    
    @Test
    public void testNineB() {
        test("C0C1C2C3C4C5C6C7C8 0:1(2),0:2(1),0:3(1),2:4(1),4:5(2),5:6(1),6:7(1),6:8(1)");
    }

}
