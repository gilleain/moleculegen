package combinatorics;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

/**
 * List all the 2^n subsets of a set. From CAGES pages 33-34.
 * 
 * @author maclean
 *
 * @param <T> the type of the elements of the set
 */
public class SubsetLister<T> implements Iterable<List<T>> {
    
    private final List<T> elements;
    
    private final int size;
    
    public SubsetLister(List<T> elements) {
        this.elements = elements;
        this.size = elements.size();
    }
    
    public int lexRank(BitSet subSetVector) {
        int rank = 0;
        for (int i = 1; i <= size; i++) {
            if (subSetVector.get(i)) {
                rank += Math.pow(2, size - i);
            }
        }
        return rank;
    }
    
    public List<T> lexUnrank(int rank) {
        double r = (double) rank;
        List<T> subSet = new ArrayList<T>();
        for (int i = size; i > 0; i--) {
            if (r % 2 == 1) {
                subSet.add(elements.get(i - 1));
            }
            r = Math.floor(r / 2);
        }
        return subSet;
    }

    public Iterator<List<T>> iterator() {
        final int maxRank = (int) Math.pow(2, size);
        
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
    
    public static SubsetLister<Integer> getIndexLister(int n) {
    	List<Integer> indices = new ArrayList<Integer>();
    	for (int i = 0; i < n; i++) {
    		indices.add(i);
    	}
    	return new SubsetLister<Integer>(indices);
    }
}
