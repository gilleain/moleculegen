package test.setorbit;

import group.Permutation;
import group.PermutationGroup;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.junit.Test;

import setorbit.BruteForcer;
import setorbit.MinimisingLister;
import setorbit.SetOrbit;

public class TestMinimisingLister {
    
    @Test
    public void testStab() {
        PermutationGroup cubeGroup = getCubeGroup();
        MinimisingLister m = new MinimisingLister();
        for (int i = 0; i < cubeGroup.getSize(); i++) {
            PermutationGroup stab = m.stabilizer(cubeGroup, i);
            for (Permutation p : stab.all()) {
                System.out.println(i + "\t" + p);
            }
        }
    }
    
    @Test
    public void testSingleSet() {
        PermutationGroup cubeGroup = getCubeGroup();
        List<Integer> s = list(0, 3, 5);
        test(s, cubeGroup);
    }
    
    @Test
    public void testIsMappedToSubset() {
        PermutationGroup cubeGroup = getCubeGroup();
        List<Integer> s = list(1, 2, 7);
        SetOrbit orbit = new BruteForcer().getInOrbit(s, cubeGroup);
        for (List<Integer> subset : orbit) {
            test(subset, cubeGroup);
        }
    }
    
    private PermutationGroup getCubeGroup() {
        List<Permutation> generators = new ArrayList<Permutation>();
        generators.add(new Permutation(1, 3, 5, 7, 0, 2, 4, 6));
        generators.add(new Permutation(1, 3, 0, 2, 5, 7, 4, 6));
        return new PermutationGroup(8, generators);
    }
    
    private void test(List<Integer> subset, PermutationGroup group) {
        boolean isMinimal = new MinimisingLister().isMappedToSubset(subset, group);
        System.out.println(isMinimal + "\t" + subset);
    }
    
    private List<Integer> list(int... elements) {
        List<Integer> list = new ArrayList<Integer>();
        for (int e : elements) {
            list.add(e);
        }
        return list;
    }

}
