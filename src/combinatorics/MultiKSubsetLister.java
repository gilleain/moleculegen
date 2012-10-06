package combinatorics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * List k-multisets of size k from a set of size n.
 * 
 * NOTE : Inefficient method that converts k-subsets of m-sets into 
 * k-multisubsets of n-sets.
 *  
 * @author maclean
 *
 */
public class MultiKSubsetLister<T> implements Iterable<List<T>> {
	
	private final List<T> elements;
	
	private final int k;
	
	private final int size;
	
	private final KSubsetLister<Integer> indexSubsetLister;
	
	public MultiKSubsetLister(int k, List<T> elements) {
        this.elements = elements;
        this.size = elements.size();
        this.k = k;
        indexSubsetLister = KSubsetLister.getIndexLister(k, size + k - 1);
    }
	
	public List<T> lexUnrank(int rank) {
		List<T> list = new ArrayList<T>();
		List<Integer> setIndices = indexSubsetLister.lexUnrank(rank);
		int y = setIndices.get(0);
		int last = -1;
		for (int i = 0; i < setIndices.size(); i++) {
			int current = setIndices.get(i);
			if (i == 0) {
				list.add(elements.get(y));
			} else {
				if (last == current - 1) {
					list.add(elements.get(y));
				} else {
					y += current - last - 1;
					list.add(elements.get(y));
				}
			}
			last = current;
		}
//		System.out.println(rank + "\t" + setIndices + "\t" + list);
		return list;
	}

	@Override
	public Iterator<List<T>> iterator() {
		final int maxRank = KSubsetLister.choose(size + k - 1, k);
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
}
