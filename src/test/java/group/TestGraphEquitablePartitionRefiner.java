package group;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import group.graph.GraphEquitablePartitionRefiner;
import group.graph.GraphRefinable;
import model.Graph;

public class TestGraphEquitablePartitionRefiner {
    
    private Partition refine(Graph g, Partition coarse) {
        GraphRefinable refinable = new GraphRefinable(g);
        GraphEquitablePartitionRefiner refiner = new GraphEquitablePartitionRefiner(refinable);
        Partition finer = refiner.refine(coarse);
        System.out.println(finer);
        return finer;
    }
    
    private void assertPartition(Partition coarse, Graph g, Partition expected) {
        assertEquals(expected, refine(g, coarse));
    }
    
    @Test
    public void cages_example_7_6() {
        Graph g = new Graph(
                "C0C1C2C3C4C5C6C7 "
                + "0:1(1),0:3(1),0:7(1),1:2(1),1:4(1),2:3(1),2:6(1),3:4(1),4:5(1),5:6(1),5:7(1),6:7(1)");
        Partition coarse   = Partition.fromString("0|1,2,3,4,5,6,7");
        Partition expect = Partition.fromString("0|1,3|7|2,4|5,6");
        assertPartition(coarse, g, expect);
    }
    
    @Test
    public void testA() {
        Partition coarse = Partition.fromString("0|1,2,3,4,5,6,7,8");
        Partition expect = Partition.fromString("0|1|2,3|6|4|5|7,8");
        assertPartition(
                coarse,
                new Graph("C0C1C2C3C4C5C6C7C8 0:1(1),0:2(1),0:3(1),1:4(2),4:5(1),5:6(1),6:7(2),6:8(1)"),
                expect);
    }
    
    @Test
    public void bipartiteUncolored() {
        Partition coarse = Partition.fromString("0|1,2,3,4,5,6");
        Partition expect = Partition.fromString("0|4,5,6|1,2,3");
        Graph g = new Graph("C0C1C2C3C4C5C6 "
                + "0:4(1),0:5(1),0:6(1),1:4(1),1:5(1),1:6(1),2:4(1),2:5(1),2:6(1),3:4(1),3:5(1),3:6(1)");
        assertPartition(coarse, g, expect);
    }
    
    @Test
    public void eqNotOrb() {
        Partition coarse = Partition.fromString("0|1,2,3,4,5,6,7,8,9");
        Partition expect = Partition.fromString("0|2|1,3|4|5|6,8|7,9");
        assertPartition(
                coarse,
                new Graph("C0C1C2C3C4C5C6C7C8C9 "
                + "0:1(1),0:2(1),0:3(1),1:2(1),1:4(1),2:3(1),3:4(1),"
                + "4:5(1),5:6(1),5:8(1),6:7(1),6:9(1),7:8(1),7:9(1),8:9(1)"),
                expect);
    }

}
