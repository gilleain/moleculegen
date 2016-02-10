package combinatorics;

import group.Partition;

/**
 * This class calculates partitions of integers; the 'partitions' of a number
 * are all the mutisets of numbers that sum to that number. For example, the
 * number 6 has partitions [3, 2, 1] and [2, 2, 2].
 * 
 * The algorithms used are from the book 'Combinatorial Algorithms : Generation,
 * Enumeration, and Search' or C.A.G.E.S. 
 * 
 * @author maclean
 *
 */
public class PartitionCalculator {
    
    /**
     * Break a number <code>m</code> up into all lists of numbers of length
     * <code>n</code> that when summed equal <code>m</code>. In other words, 
     * generate all partitions of m with n parts.
     * 
     * @param m the number to partition
     * @param n the number of parts
     * @return an array of Partitions
     */
    public static Partition[] partition(int m, int n) {
        int[][] counts = countPartitions(m, n);
        int numberOfPartitions = counts[m][n];
        Partition[] partitions = new Partition[numberOfPartitions];
        for (int rank = 0; rank < numberOfPartitions; rank++) {
            partitions[rank] = lexicographicallyUnrank(m, n, rank, counts);
        }
        return partitions;
    }
    
    /**
     * Count up all the partitions of <code>m</code> with <code>n</code> parts.
     * Note that this algorithm does not actually generate the partitions, it
     * only counts how many there are; this information is used in the method
     * to actually do the enumeration.
     * 
     * (From the book C.A.G.E.S. - renamed from EnumPartitions(m, n) as it seems
     * more like counting than enumerating).
     * 
     * So, each cell in the array is the count of partitions for (m, n) where
     * the first index is m and the second, n.  
     * 
     * @param m the integer to count the partitions of
     * @param n the number of parts in each partition to count
     * @return a table of counts for partitions of m with n parts
     */
    public static int[][] countPartitions(int m, int n) {
        int[][] counts = new int[m + 1][];
        
        counts[0] = new int[n + 1];
        counts[0][0] = 1;
        
        for (int i = 1; i <= m; i++) {
            counts[i] = new int[n + 1];
            counts[i][0] = 0;
            for (int j = 1; j <= Math.min(i, n); j++) {
                if (i < 2 * j) {
                    counts[i][j] = counts[i - 1][j - 1];
                } else {
                    counts[i][j] = counts[i - 1][j - 1] + counts[i - j][j];
                }
            }
        }
        return counts;
    }
    
    /**
     * Generate a partition of <code>m</code> with <code>n</code> parts, and
     * rank <code>r</code>. The 'rank' of a partition is the position of the
     * partition in a lexicographically ordered list.
     * 
     * @param m the integer to partition
     * @param n the number of parts the partition should have
     * @param r the rank of the partition to generate
     * @param counts pre-computed counts of the numbers of partitions
     * @return the Partition of m with n parts with rank r 
     */
    public static Partition lexicographicallyUnrank(int m, int n, int r) {
        return lexicographicallyUnrank(m, n, r, countPartitions(m, n));
    }
    
    /**
     * Generate a partition of <code>m</code> with <code>n</code> parts, and
     * rank <code>r</code>. The 'rank' of a partition is the position of the
     * partition in a lexicographically ordered list.
     * 
     * @param m the integer to partition
     * @param n the number of parts the partition should have
     * @param r the rank of the partition to generate
     * @param counts pre-computed counts of the numbers of partitions
     * @return the Partition of m with n parts with rank r
     */
    public static Partition lexicographicallyUnrank(
            int m, int n, int r, int[][] counts) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) { a[i] = 0; }
        while (m > 0) {
            if (r < counts[m - 1][n - 1]) {
                a[n - 1]++;
                m--;
                n--;
            } else {
                for (int i = 0; i < n; i++) {
                    a[i]++;
                }
                r -= counts[m - 1][n - 1];
                m -= n;
            }
        }
        return new Partition(a);
    }
}