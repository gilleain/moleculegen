package group;

import org.junit.Test;

import group.graph.GraphEquitablePartitionRefiner;
import group.graph.GraphRefinable;
import model.Graph;

public class TestGraphEquitablePartitionRefiner {
    
    private void refine(Graph g, Partition coarse) {
        GraphRefinable refinable = new GraphRefinable(g);
        GraphEquitablePartitionRefiner refiner = new GraphEquitablePartitionRefiner(refinable);
        Partition finer = refiner.refine(coarse);
        System.out.println(finer);
    }
    
    @Test
    public void cages_example_7_6() {
        Graph g = new Graph(
                "C0C1C2C3C4C5C6C7 "
                + "0:1(1),0:3(1),0:7(1),1:2(1),1:4(1),2:3(1),2:6(1),3:4(1),4:5(1),5:6(1),5:7(1),6:7(1)");
        refine(g, Partition.fromString("0|1,2,3,4,5,6,7"));
    }
    
    @Test
    public void testA() {
        refine(new Graph("C0C1C2C3C4C5C6C7C8 0:1(1),0:2(1),0:3(1),1:4(2),4:5(1),5:6(1),6:7(2),6:8(1)"),
                Partition.fromString("0|1,2,3,4,5,6,7,8"));
    }

}
