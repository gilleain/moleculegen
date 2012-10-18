package group;

import java.util.Arrays;
import java.util.List;

public class OrbitLister {
    
    public int[][] reconstructOrbits(int[][] orbitList, int k) {
        int[][] orbits = new int[orbitList.length][];
        for (int j = 0; j < orbitList.length; j++) {
            int o = orbitList[j].length;
            orbits[j] = new int[k];
            for (int l = 0; l < o; l++) {
                indexToSet(k, orbitList[j][l], orbits[j]);
            }
        }
        return orbits;
    }
    
    public int[][] getOrbits(int kvalue, int size, List<Permutation> permutations) {
        int nchoosek = nChooseK(size, kvalue);
        DisjointSetForest orbits = new DisjointSetForest(nchoosek);
        for (Permutation perm : permutations) {
            use_generator_kset(perm, nchoosek, kvalue, orbits);
        }
        
        System.out.println(orbits);
        
        int[][] orbitList = getOrbits(nchoosek, orbits);
        return reconstructOrbits(orbitList, kvalue);
    }
    
    public int[][] getOrbits(int kvalue, int size, PermutationGroup group) {
        return getOrbits(kvalue, size, group.all());
    }
    
    public int[][] getOrbits(int nchoosek, DisjointSetForest orbits) {
        int numOrbits = 0;
        for (int i = 0; i < nchoosek; i++) {
            if (orbits.get(i) < 0) {
                numOrbits++;
            }
        }
        int[][] orbitList = new int[numOrbits][];
        int curOrbit = 0;
        for (int i = 0; i < nchoosek; i++) {
            if (orbits.get(i) < 0) {
                int oLength = 1 - orbits.get(i) - 1;
                orbitList[curOrbit] = new int[oLength];
                int curIndex = 0;
                for (int l = 0; l < nchoosek; l++) {
                    if (orbits.getRoot(l) == i) {
                        orbitList[curOrbit][curIndex] = l;
                        curIndex++;
                    }
                }
                curOrbit++;
            }
        }
        return orbitList;
    }
    
    public void use_generator_kset(Permutation perm, 
                                   int nchoosek,
                                   int kvalue,
                                   DisjointSetForest orbits) {
        int k = kvalue;
        
        int[] new_partitions = new int[nchoosek];
        
        for (int i = 0; i < nchoosek; i++) {
            new_partitions[i] = nchoosek + 1;
        }
        
        int[] start_set = new int[k];
        int[] perm_set = new int[k];
        
        /* for all objects, by increasing rank */
        for (int start_index = 0; start_index < nchoosek; start_index++) {
            if (new_partitions[start_index] >= nchoosek) {
                
                /* we have not found anything previously that hits this element */
                indexToSet(k, start_index, start_set);
                
                /* loop with generator! */
                new_partitions[start_index] = start_index;
                
                for (int i = 0; i < k; i++) {
                    perm_set[i] = perm.get(start_set[i]);
                }
                
                int pindex = indexOfSet(k, perm_set);
                
                /* wait until ordered pair match */
                while (pindex != start_index) {
                    
                    /* join these orbits! */
                    orbits.makeUnion(start_index, pindex);
                    
                    new_partitions[pindex] = start_index;
                    
                    for (int i = 0; i < k; i++) {
                        perm_set[i] = perm.get(perm_set[i]);
                    }
                    
                    pindex = indexOfSet(k, perm_set);
                }
            }
        }
    }
    
    private int indexOfSet(int k, int[] perm_set) {
        Arrays.sort(perm_set);
        return recurseIndexOfSet(k, perm_set);
    }
    
    private int recurseIndexOfSet(int size, int[] set) {
        if (size <= 0) {
            return 0;
        } else if (size == 1) {
            return set[0];
        } else if (size == 2) {
            return indexOf(set[0], set[1]);
        }
        
        int base = nChooseK(set[size - 1], size);
        int offset = recurseIndexOfSet(size - 1, set);
        
        return base + offset;
    }
    
    private int indexOf(int i, int j) {
        if (i == j) {   // bad input
            return -1;
        }
        
        if (j < i) {    // wrong order
            return indexOf(j, i);
        }
        
        return nChooseK(j, 2) + i;
    }

    private void indexToSet(int size, int index, int[] set) {
        for (int i = size - 1; i >= 0; i--) {
            /* find ith bit by considering largest portion of index */
            /* then lower for the next position */

            /* we need to solve for largest s with (s choose (i+1)) <= index */
            int min_elt = i;
            int max_elt = 2 * (i + 1);

            /* find the right frame */
            while (nChooseK(max_elt, i + 1) <= index) {
                min_elt = max_elt;
                max_elt = max_elt << 1; /* double */
            }

            /* do binary search */
            while ( min_elt <= max_elt ) {
                int half = (max_elt + min_elt) >> 1;

                if (nChooseK(half, i + 1) <= index) {
                    min_elt = half + 1;
                } else {
                    max_elt = half - 1;
                }
            }

            /* place this value */
            set[i] = min_elt - 1;

            /* modify index */
            index -= nChooseK(set[i], i + 1);
        }
    }
    
    private int nChooseK(int n, int k) {
        if (k > n) {
            return 0;
        }
        
        if (k == n) {
            return 1;
        }
        
        double N = n;
        double K = k;
        
        double nCk = 1.0;
        for (double i = 0; i < k; i++) {
            nCk *= ((N - i) / (K - i));
        }
        return (int) Math.round(nCk);
    }

}
