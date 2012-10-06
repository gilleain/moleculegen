package group;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A permutation with some associated methods to multiply, invert, and convert
 * to cycle strings. Much of the code in this was implemented from the 
 * C.A.G.E.S. book.
 * 
 * @author maclean
 * @cdk.module group
 *
 */
public class Permutation  {

    private int[] values;

    /**
     * Constructs an identity permutation with <code>size</code> elements.
     * 
     * @param size the number of elements in the permutation
     */
    public Permutation(int size) {
        this.values = new int[size];
        for (int i = 0; i < size; i++) {
            this.values[i] = i;
        }
    }
    
    public Permutation(int... values) {
        this.values = values;
    }
    
    public Permutation(Permutation other) {
        this.values = other.values.clone();
    }
    
    public boolean equals(Object other) {
        if (other instanceof Permutation) {
            return Arrays.equals(values, ((Permutation)other).values);
        } else {
            return false;
        }
    }
    
    public boolean isIdentity() {
        for (int i = 0; i < this.values.length; i++) {
            if (this.values[i] != i) {
                return false;
            }
        }
        return true;
    }
    
    public int size() {
        return this.values.length;
    }
    
    public int get(int i) {
        return this.values[i];
    }
    
    public int[] getValues() {
        return this.values;
    }
    
    public SortedSet<Integer> apply(SortedSet<Integer> set) {
        SortedSet<Integer> newSet = new TreeSet<Integer>();
        int h = 0;
        int u = set.last();
        int z = set.size();
        System.out.println("applying " + this + " to " + set);
        while (h < z) {
            h++;
            newSet.add(this.values[u]);
            if (h < z) u = set.headSet(u).last();
        }
        return newSet;
    }
    
    /**
     * Find an r such that this[r] != other[r].
     * @param other the other permutation to compare with
     * @return the first point at which the two permutations differ
     */
    public int firstIndexOfDifference(Permutation other) {
        int r = 0;
        while ((r < this.values.length) && this.values[r] == other.get(r)) {
            r++;
        }
        return r;
    }
    
    public void set(int index, int value) {
        this.values[index] = value;
    }

    public void setTo(Permutation other) {
        for (int i = 0; i < this.values.length; i++) {
            this.values[i] = other.values[i];
        }
    }

    // TODO : this is the same as "this = indices.multiply(newValues.invert());"
    // XXX could just delete it (and the other set method, below)
    public void set(Permutation indices, Permutation newValues) {
        this.set(indices.values, newValues.values);
    }

    public void set(int[] indices, int[] newValues) {
        // if this is 'p' and indices is 'q' and newValues is 'r':
        // { p[q[i]] = r[i] : i in N }
        for (int i = 0; i < this.values.length; i++) {
            this.values[indices[i]] = newValues[i];
        }
    }

    public Permutation multiply(Permutation other) {
        Permutation newPermutation = new Permutation(this.values.length);
        for (int i = 0; i < this.values.length; i++) {
            newPermutation.values[i] = this.values[other.values[i]];
        }
        return newPermutation;
    }
    
    public Permutation invert() {
        Permutation inversion = new Permutation(this.values.length);
        for (int i = 0; i < this.values.length; i++) {
            inversion.values[this.values[i]] = i;
        }
        return inversion;
    }
    
    /**
     * Gets the number of cycles of length l in the cycle decomposition 
     * of this permutation.
     *  
     * @return the number of cycles of each length
     */
    public int[] getType() {
        int n = this.values.length;
        int[] type = new int[n + 1];
        boolean[] p = new boolean[n];
        Arrays.fill(p, true);
        
        for (int i = 0; i < n; i++) {
            if (p[i]) {
                int l = 1;
                int j = i;
                p[j] = false;
                while (p[this.values[j]]) {
                    l++;
                    j = this.values[j];
                    p[j] = false;
                }
                type[l]++;
            }
        }
        return type;
    }
    
    public static Permutation fromCycleString(String cycleString, int size) {
        Permutation p = new Permutation(size);
        int index = 0;
        int x = 0;
        int y = 0;
        int z = 0;
        while (index < cycleString.length()) {
            char c = cycleString.charAt(index);
            if (c == '(') {
                index++;
                c = cycleString.charAt(index);
                if (Character.isDigit(c)) {
                  x = Integer.parseInt(cycleString.substring(index, index + 1));
                  z = x;
                  index++;  // TODO > 9!
                }
            }
            c = cycleString.charAt(index);
            if (c == ' ' || c == ',') {
                index++;
                c = cycleString.charAt(index);
                if (Character.isDigit(c)) {
                    y = Integer.parseInt(cycleString.substring(index, index + 1));
                    p.set(x, y);
                    index++;  // TODO > 9!
                    x = y;
                }
            }
            c = cycleString.charAt(index);
            if (c == ')') {
                p.set(x, z);
                index++;
            }
        }
        return p;
    }
    
    public String toCycleString() {
        int n = this.values.length;
        boolean[] p = new boolean[n];
        Arrays.fill(p, true);
        
        StringBuffer sb = new StringBuffer();
        int j = 0;
        for (int i = 0; i < n; i++) {
            if (p[i]) {
                sb.append('(');
                sb.append(i);
                p[i] = false;
                j = i;
                while (p[values[j]]) {
                    sb.append(", ");
                    j = values[j];
                    sb.append(j);
                    p[j] = false;
                }
                sb.append(')');
            }
        }
        return sb.toString();
    }
    
    public String toString() {
        return Arrays.toString(this.values);
    }

}
