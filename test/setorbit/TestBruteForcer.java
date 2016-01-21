package setorbit;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import group.Permutation;
import group.PermutationGroup;

public class TestBruteForcer {
    
    @Test
    public void cube() {
        List<Permutation> generators = new ArrayList<Permutation>();
        generators.add(new Permutation(1, 3, 5, 7, 0, 2, 4, 6));
        generators.add(new Permutation(1, 3, 0, 2, 5, 7, 4, 6));
        PermutationGroup cubeGroup = new PermutationGroup(8, generators);
        List<Integer> s = list(1, 2, 7);
        SetOrbit orbits = new BruteForcer().getInOrbit(s, cubeGroup);
        List<List<Integer>> expected = new ArrayList<List<Integer>>();
        expected.add(list(0, 3, 5));
        expected.add(list(0, 3, 6));
        expected.add(list(0, 5, 6));
        expected.add(list(1, 2, 4));
        expected.add(list(1, 2, 7));
        expected.add(list(1, 4, 7));
        expected.add(list(2, 4, 7));
        expected.add(list(3, 5, 6));
        
        listContains(orbits, expected);
    }
    
    private List<Integer> list(int... elements) {
        List<Integer> list = new ArrayList<Integer>();
        for (int e : elements) {
            list.add(e);
        }
        return list;
    }
    
    private void listContains(SetOrbit observedList, List<List<Integer>> expectedList) {
        for (List<Integer> observed : observedList) {
            assertTrue(expectedList.contains(observed));
        }
    }

}
