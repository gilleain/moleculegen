package setorbit;

import group.Permutation;
import group.PermutationGroup;
import group.PermutationGroup.Backtracker;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/** 
 * Naive implementation of the algorithm described in "Enumerating Set Orbits" by 
 * Christian Pech and Sven Reichard for determining the orbit of a set under a group. 
 * 
 * @author maclean
 *
 */
public class MinimisingLister {
    
    public SetOrbit getInOrbit(List<Integer> s, PermutationGroup g) {
        SetOrbit orbits = new SetOrbit();
        // TODO
        return orbits;
    }
    
    public boolean isMappedToSubset(List<Integer> s, PermutationGroup g) {
        BitSet t = new BitSet(s.size());
        for (int element : s) {
            t.set(element);
        }
        return isMappedToSubset(s, t, g);
    }
    
    private boolean isMappedToSubset(List<Integer> s, BitSet t, PermutationGroup g) {
        if (t.cardinality() == 0 || s.isEmpty()) return false;
        int t0 = t.nextSetBit(0);
        List<Permutation> all = g.all();
//        System.out.println("Testing " + t0 + " against " + s);
        for (Permutation p : all) {
            for (int elemS : s) {
//                System.out.println(elemS + " : " + p + " : "+ p.get(elemS) + " < " + t0);
                if (p.get(elemS) < t0) return true;
            }
        }
//        System.out.println("Finished testing " + t0);
        
        for (int elemS : s) {
            for (Permutation p : all) {
                if (p.get(elemS) == t0) {
                    List<Integer> sPrime = removeAndPermute(s, elemS, p);
                    t.clear(t0);
                    PermutationGroup gPrime = stabilizer(g, t0);
                    if (isMappedToSubset(sPrime, t, gPrime)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public PermutationGroup stabilizer(PermutationGroup g, final int x) {
       final List<Permutation> result = new ArrayList<Permutation>();
       g.apply(new Backtracker() {
            
            @Override
            public boolean finished() {
                return false;
            }
            
            @Override
            public void applyTo(Permutation p) {
                if (p.get(x) == x) {
                    result.add(p);
                }
            }
       });
       return new PermutationGroup(g.getSize(), result);
    }

    private List<Integer> removeAndPermute(List<Integer> s, int elemS, Permutation p) {
        List<Integer> sPrime = new ArrayList<Integer>();
        for (int e : s) {
            if (e == elemS) {
                continue;
            } else {
                sPrime.add(p.get(e));
            }
        }
//        System.out.println("Permuted " + s + " by " + p + " to get " + sPrime);
        return sPrime;
    }

}
