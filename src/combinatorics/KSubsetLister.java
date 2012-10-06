package combinatorics;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

/**
 * List all the k-subsets of size k of a set. From CAGES pages ??.
 * 
 * @author maclean
 *
 * @param k the size of the subset
 * @param <T> the type of the elements of the set
 */
public class KSubsetLister<T> implements Iterable<List<T>> {
    
    private final List<T> elements;
    
    private final int size;
    
    private final int k;
    
    public KSubsetLister(int k, List<T> elements) {
        this.elements = elements;
        this.size = elements.size();
        this.k = k;
    }
    
    private static int[] getBinomials(int n) {
    	int[] b = new int[n + 1];
    	b[0] = 1;
    	for (int i = 1; i <= n; ++i) {
    		b[i] = 1;
    		for (int j = i - 1; j > 0; --j) {
    			b[j] += b[j - 1];
    		}
    	}
    	return b;
    }
    
    public static int choose(int n, int k) {
    	return getBinomials(n)[k];
    }
    
    public int lexRank(BitSet subSetVector) {
       return -1;	// TODO!
    }
    
    public List<T> lexUnrank(int rank) {
    	int x = 1;
    	List<T> t = new ArrayList<T>();
    	for (int i = 1; i <= k; i++) {
    		
    		while (choose(size - x, k - i) <= rank) {
    			rank -= choose(size - x, k - i);
    			x += 1;
    		}
    		t.add(elements.get(x - 1));
    		x += 1;
    	}
    	return t;
    }

    public Iterator<List<T>> iterator() {
        final int maxRank = choose(size, k);
        
        return new Iterator<List<T>>() {
            int rank = 0;

            public boolean hasNext() {
                return rank < maxRank;
            }

            public List<T> next() {
                List<T> nextSubSet = lexUnrank(rank);
                rank++;
                return nextSubSet;
            }

            public void remove() {
                rank++;
            }
        };
    }
    
    public static KSubsetLister<Integer> getIndexLister(int k, int n) {
    	List<Integer> indices = new ArrayList<Integer>();
    	for (int i = 0; i < n; i++) {
    		indices.add(i);
    	}
    	return new KSubsetLister<Integer>(k, indices);
    }
}
