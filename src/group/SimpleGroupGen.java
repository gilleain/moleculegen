package group;

import java.util.ArrayList;
import java.util.List;

/**
 * Useful for testing - makes groups from a set of generators by brute force.
 * 
 * @author maclean
 *
 */
public class SimpleGroupGen {
    
    public static List<Permutation> generate(int n, List<Permutation> generators) {
        List<Permutation> all = new ArrayList<Permutation>();
        List<Permutation> newP = new ArrayList<Permutation>();
        newP.add(new Permutation(n));
        while (!newP.isEmpty()) {
            all.addAll(newP);
            List<Permutation> lastP = new ArrayList<Permutation>(newP);
            newP = new ArrayList<Permutation>();
            for (Permutation g : generators) {
                for (Permutation h : lastP) {
                    Permutation f = g.multiply(h);
                    if (all.contains(f) || newP.contains(f)) {
                        continue;
                    } else {
                        newP.add(f);
                    }
                }
            }
        }
        return all;
    }

}
