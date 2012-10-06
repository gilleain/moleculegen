package group;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * A permutation group with a Schreier-Sims representation. For a number n, a
 * list of permutation sets is stored (U0,...,Un-1). All n! permutations of
 * [0...n-1] can be reconstructed from this list by backtracking - see, for
 * example, the <a href="#generateAll">generateAll</a> method.
 * </p>
 * 
 * <p>
 * So if G is a group on X = {0, 1, 2, 3, ..., n-1}, then:
 * 
 * <pre>
 *      G<sub>0</sub> = {g &isin; G  : g(0) = 0}
 *      G<sub>1</sub> = {g &isin; G<sub>0</sub> : g(1) = 1}
 *      G<sub>2</sub> = {g &isin; G<sub>1</sub> : g(2) = 2}
 *      ...
 *      G<sub>n-1</sub> = {g in G<sub>n-2</sub> : g(n - 1) = n - 1} = {I}
 * </pre>
 * 
 * and G<sub>0</sub>, G<sub>1</sub>, G<sub>2</sub>, ..., G<sub>n-1</sub> are
 * subgroups of G.
 * </p>
 * 
 * <p>
 * Now let orb(0) = {g(0) : g &isin; G} be the orbit of 0 under G. Then |orb(0)|
 * (the size of the orbit) is n<sub>0</sub> for some integer 0 < n<sub>0</sub>
 * <= n and write orb(0) = {x<sub>0,1</sub>, x<sub>0,2</sub>, ...,
 * x<sub>0,n<sub>0</sub></sub>} and for each i, 1 <= i <= n<sub>0</sub> choose
 * some h<sub>0,1</sub> in G such that h<sub>0,i</sub>(0) = x<sub>0,1</sub>. Set
 * U<sub>0</sub> = {h<sub>0,1</sub>, ..., h<sub>0,n<sub>0</sub></sub>}.
 * </p>
 * 
 * <p>
 * Given the above, the list of permutation sets in this class is
 * [U<sub>0</sub>,..,U<sub>n</sub>]. Also, for all i = 1, ..., n-1 the set U<sub>i</sub> is
 * a left transversal of G<sub>i</sub> in G<sub>i-1</sub>.
 * </p>
 * 
 * <p>
 * This is port of the code from the C.A.G.E.S. book by Kreher and Stinson. The
 * mathematics in the description above is also from that book (pp. 203).
 * </p>
 * 
 * @author maclean
 * @cdk.module group
 * 
 */
public class SSPermutationGroup {
    
    /**
     * An interface for use with the apply method, which runs through all the
     * permutations in this group.
     *
     */
    public interface Backtracker {
        
        /**
         * Do something to the permutation
         * @param p a permutation in the full group
         */
        public void applyTo(Permutation p);
        
        public boolean finished();
    }
 
    /**
     * The compact list of permutations that make up this group
     */
    private Permutation[][] permutations;
    
    /**
     * The size of the group - strictly, the size of the permutation 
     */
    private int n;
    
    /**
     * The base of the group
     */
    private Permutation base;
    
    public SSPermutationGroup(int n) {
        this(new Permutation(n));
    }
    
    /**
     * Creates the initial group, with the base <code>base</code>.
     * 
     * @param base the permutation that the group is based on
     */
    public SSPermutationGroup(Permutation base) {
        this.n = base.size();
        this.base = new Permutation(base);
        this.permutations = new Permutation[n][n];
        for (int i = 0; i < n; i++) {
            this.permutations[i][this.base.get(i)] = new Permutation(n);
        }
    }
    
    /**
     * Creates a group from a set of generators. See the makeSymN method for
     * where this is used to make the symmetric group on N using the two 
     * generators (0, 1) and (1, 2, ..., n - 1, 0)
     * 
     * @param n the size of the group
     * @param generators the generators to use to make the group
     */
    public SSPermutationGroup(int n, List<Permutation> generators) {
        this(new Permutation(n));
        for (Permutation generator : generators) {
            this.enter(generator);
        }
    }
    
    /**
     * Make the symmetric group Sym(N) for N. That is, a group of permutations
     * that represents _all_ permutations of size N.
     * 
     * @param n the size of the permutation
     * @return a group for all permutations of N
     */
    public static SSPermutationGroup makeSymN(int n) {
        List<Permutation> generators = new ArrayList<Permutation>();
        
        // p1 is (0, 1)
        int[] p1 = new int[n];
        p1[0] = 1;
        p1[1] = 0;
        for (int i = 2; i < n; i++) {
            p1[i] = i;
        }
        
        // p2 is (1, 2, ...., n, 0)
        int[] p2 = new int[n];
        p2[0] = 1;
        for (int i = 1; i < n - 1; i++) {
            p2[i] = i + 1;
        }
        p2[n - 1] = 0;
        
        generators.add(new Permutation(p1));
        generators.add(new Permutation(p2));
        
        return new SSPermutationGroup(n, generators);
    }
    
    /**
     * Calculates the size of the group.
     *  
     * @return the (total) number of permutations in the group 
     */
    public int order() {
        int total = 1;
        for (int i = 0; i < this.n; i++) {
            int sum = 0;
            for (int j = 0; j < this.n; j++) {
                if (this.permutations[i][j] != null) {
                    sum++;
                }
            }
            total *= sum;
        }
        return total;
    }
    
    // XXX : this is nasty, but a group may have a size larger than 
    // Integer.MAX_INTEGER (2 ** 32 - 1) - for example sym(13) is larger.
    public long orderAsLong() {
        long total = 1;
        for (int i = 0; i < this.n; i++) {
            int sum = 0;
            for (int j = 0; j < this.n; j++) {
                if (this.permutations[i][j] != null) {
                    sum++;
                }
            }
            total *= sum;
        }
        return total;
        
    }
    
    /**
     * Get one of the permutations that make up the compact representation.
     * 
     * @param i the index of the set U.
     * @param j the index of the permutation within Ui.
     * @return a permutation
     */
    public Permutation get(int i, int j) {
        return this.permutations[i][j];
    }
    
    
    /**
     * Get the traversal U<sub>i</sub> from the list of transversals.
     * 
     * @param i the index of the transversal
     * @return a list of permutations
     */
    public List<Permutation> getLeftTransversal(int i) {
        List<Permutation> traversal = new ArrayList<Permutation>();
        for (int j = 0; j < this.n; j++) {
            if (this.permutations[i][j] != null) {
                traversal.add(this.permutations[i][j]);
            }
        }
        return traversal;
    }
    
    /**
     * Generate a transversal of a subgroup in this group. 
     * 
     * @param subgroup the subgroup to use for the transversal
     * @return a list of permutations
     */
    public List<Permutation> transversal(final SSPermutationGroup subgroup) {
        final int size = n;
        final int m = this.order() / subgroup.order();
        final List<Permutation> results = new ArrayList<Permutation>();
        Backtracker transversalBacktracker = new Backtracker() {

            private boolean finished = false;
            
            public void applyTo(Permutation p) {
                for (Permutation f : results) {
                    Permutation h = f.invert().multiply(p);
                    if (subgroup.test(h) == size) {
                        return;
                    }
                }
                results.add(p);
                if (results.size() >= m) {
                    this.finished = true;
                }
            }

            public boolean finished() {
                return finished;
            }
            
        };
        this.apply(transversalBacktracker);
        return results;
    }
    
    /**
     * Apply the backtracker to all permutations in the larger group.
     * 
     * @param backtracker a hook for acting on the permutations
     */
    public void apply(Backtracker backtracker) {
        this.backtrack(0, new Permutation(this.n), backtracker);
    }
    
    private void backtrack(int l, Permutation g, Backtracker backtracker) {
        if (l == this.n) {
            backtracker.applyTo(g);
        } else {
            for (int i = 0; i < this.n; i++) {
                Permutation h = this.permutations[l][i]; 
                if (h != null) {
                    backtrack(l + 1, g.multiply(h), backtracker);
                }
            }
        }
    }
    
    /**
     * Generate the whole group from the compact list of permutations.
     * 
     * @return a list of permutations
     */
    public List<Permutation> all() {
        final List<Permutation> permutations = new ArrayList<Permutation>();
        Backtracker counter = new Backtracker() {
            public void applyTo(Permutation p) {
                permutations.add(p);
            }
            public boolean finished() { return false; }
        };
        this.apply(counter);
        return permutations;
    }

    /**
     * Change the base of the group to the new base <code>newBase</code>.
     * 
     * @param newBase the new base for the group
     */
    public void changeBase(Permutation newBase) {
        SSPermutationGroup H = 
            new SSPermutationGroup(newBase);
        
        int r = this.base.firstIndexOfDifference(newBase);
        
        for (int j = r; j < n; j++) {
            for (int a = 0; a < n; a++) {
                Permutation g = this.permutations[j][a];
                if (g != null) {
                    H.enter(g);
                }
            }
        }
        
        for (int j = 0; j < r; j++) {
            for (int a = 0; a < n; a++) {
                Permutation g = this.permutations[j][a];
                if (g != null) {
                    int h = H.base.get(j);
                    int x = g.get(h);
                    H.permutations[j][x] = new Permutation(g);
                }
            }
        }
        this.base = new Permutation(H.base);
        this.permutations = H.permutations.clone();
    }

    /**
     * Enter the permutation g into this group.
     * 
     * @param g a permutation
     */
    public void enter(Permutation g) {
       int deg = this.n;
       int i = test(g);
       if (i == deg) {
           return;
       } else {
           this.permutations[i][g.get(this.base.get(i))] = new Permutation(g);
       }
       
       for (int j = 0; j <= i; j++) {
           for (int a = 0; a < deg; a++) {
               Permutation h = this.permutations[j][a];
               if (h != null) {
                   Permutation f = g.multiply(h);
                   enter(f);
               }
           }
       }
    }
    
    /**
     * Test a permutation to see if it is in the group. Note that this also
     * alters the permutation passed in.
     * 
     * @param permutation the one to test
     * @return the position it should be in the group, if any
     */
    public int test(Permutation permutation) {
        for (int i = 0; i < n; i++) {
            int x = permutation.get(this.base.get(i));
            Permutation h = permutations[i][x];
            if (h == null) {
                return i;
            } else {
                permutation.setTo(h.invert().multiply(permutation));
            }
        }
        return n;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Base = ").append(this.base).append("\n");
        for (int i = 0; i < this.n; i++) {
            sb.append("U").append(i).append(" = ");
            for (int j = 0; j < this.n; j++) {
                sb.append(this.permutations[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString(); 
    }

}
