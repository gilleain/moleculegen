package group;

import java.util.Arrays;


/**
 * Implementation of a union-find data structure, largely copied from code due to Derrick Stolee.
 * 
 * @author maclean
 *
 */
public class DisjointSetForest {
    
    private int[] tree; 
    
    public DisjointSetForest(int n) {
        tree = new int[n];
        for (int i = 0; i < n; i++) {
            tree[i] = -1;
        }
    }
    
    public int get(int i) {
        return tree[i];
    }
    
    public int getRoot(int x) {
        if (tree[x] < 0) {
            return x;
        } else {
            return getRoot(tree[x]);
        }
    }
    
    public void makeUnion(int x, int y) {
        int xRoot = getRoot(x);
        int yRoot = getRoot(y);
        
        if (xRoot == yRoot) {
            return;
        }
        
        if (tree[xRoot] < tree[yRoot]) {
            tree[yRoot] = tree[yRoot] + tree[xRoot];
            tree[xRoot] = yRoot;
        } else {
            tree[xRoot] = tree[xRoot] + tree[yRoot];
            tree[yRoot] = xRoot;
        }
    }
    
    public int[][] getSets() {
        int n = 0;
        for (int i = 0; i < tree.length; i++) {
            if (tree[i] < 0) {
                n++;
            }
        }
        int[][] sets = new int[n][];
        int currentSet = 0;
        for (int i = 0; i < tree.length; i++) {
            if (tree[i] < 0) {
                int setSize = 1 - tree[i] - 1;
                sets[currentSet] = new int[setSize];
                int currentIndex = 0;
                for (int element = 0; element < tree.length; element++) {
                    if (getRoot(element) == i) {
                        sets[currentSet][currentIndex] = element;
                        currentIndex++;
                    }
                }
                currentSet++;
            }
        }
        return sets;
    }
    
    public String toString() {
        return Arrays.toString(tree);
    }

}
