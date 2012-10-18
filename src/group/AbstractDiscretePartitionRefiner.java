package group;

import java.util.Set;

/**
 * Refines vertex partitions until they are discrete, and therefore equivalent
 * to permutations. These permutations are automorphisms of the graph that was
 * used during the refinement to guide the splitting of partition blocks. 
 * 
 * @author maclean
 * @cdk.module group
 */
public abstract class AbstractDiscretePartitionRefiner {
    
    public enum Result { WORSE, EQUAL, BETTER };
    
    private boolean bestExist;
    
    private Permutation best;
    
    private Permutation first;
    
    private IEquitablePartitionRefiner equitableRefiner;
    
    private PermutationGroup group;
    
    private boolean checkVertexColors;
    
    public AbstractDiscretePartitionRefiner() {
        this(false);
    }
    
    public AbstractDiscretePartitionRefiner(boolean checkVertexColors) {
        this.checkVertexColors = checkVertexColors;
        this.bestExist = false;
        this.best = null;
        this.equitableRefiner = null;
    }
    
    public abstract int getVertexCount();
    
    public abstract int isConnected(int i, int j);
    
    public abstract boolean sameColor(int i, int j);
    
    public void setup(PermutationGroup group, IEquitablePartitionRefiner refiner) {
        this.bestExist = false;
        this.best = null;
        this.group = group;
        this.equitableRefiner = refiner;
    }
    
    public boolean firstIsIdentity() {
        return this.first.isIdentity();
    }
    
    public long getCertificate() {
        return calculateCertificate(this.getBest());
    }
    
    public long calculateCertificate(Permutation p) {
        int k = 0;
        long certificate = 0;
        int n = getVertexCount();
        for (int j = n - 1; j > 0; j--) {
        	for (int i = j - 1; i >= 0; i--) {
        		if (isConnected(p.get(i), p.get(j)) > 0) {    // XXX!!
        			certificate += (int)Math.pow(2, k);
        		}
        		k++;
        	}
        }
//        for (int i = 0; i < n - 1; i++) {
//            for (int j = i + 1; j < n; j++) {
//                if (isConnected(p.get(i), p.get(j))) {
//                    certificate += (int)Math.pow(2, k);
//                }
//                k++;
//            }
//        }
        return certificate;
    }
    
    public String getHalfMatrixString(Permutation p) {
        String hms = "";
        int n = p.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                int connectivity = isConnected(p.get(i), p.get(j)); 
                hms += String.valueOf(connectivity);    // XXX what if < 0 ?
            }
        }
        return hms;
    }
    
    public String getBestHalfMatrixString() {
       return getHalfMatrixString(best);
    }
    
    public String getFirstHalfMatrixString() {
        return getHalfMatrixString(first);
     }
    
    public String getHalfMatrixString() {
        return getHalfMatrixString(new Permutation(getVertexCount()));
    }
    
    public PermutationGroup getGroup() {
        return this.group;
    }
    
    public Permutation getBest() {
        return this.best;
    }
    
    public Permutation getFirst() {
        return this.first;
    }
    
    /**
     * Check for a canonical graph, without generating the whole 
     * automorphism group.
     * 
     * @return true if the graph is canonical
     */
    public boolean isCanonical() {
        return isCanonical(Partition.unit(getVertexCount()));
    }
    
    public boolean isCanonical(Partition partition) {
        int n = getVertexCount();
        if (partition.size() == n) {
            return partition.toPermutation().isIdentity();
        } else {
            int l = partition.getIndexOfFirstNonDiscreteCell();
            int first = partition.getFirstInCell(l);
            Partition finerPartition = 
                equitableRefiner.refine(partition.splitBefore(l, first));
            return isCanonical(finerPartition);
        }
    }
    
    public void refine(Partition p) {
        refine(this.group, p);
    }
    
    public void refine(PermutationGroup group, Partition coarser) {
//    	System.out.println(coarser);
        int vertexCount = getVertexCount();
        
        Partition finer = equitableRefiner.refine(coarser);
        
        int firstNonDiscreteCell = finer.getIndexOfFirstNonDiscreteCell();
        if (firstNonDiscreteCell == -1) {
            firstNonDiscreteCell = vertexCount;
        }
        
        Permutation pi1 = new Permutation(firstNonDiscreteCell);
        Permutation pi2 = new Permutation(vertexCount);
        
        Result result = Result.BETTER;
        if (bestExist) {
            finer.setAsPermutation(pi1, firstNonDiscreteCell);
            result = compareRowwise(pi1);
        }
        
        if (finer.size() == vertexCount) {    // partition is discrete
//        	System.out.println("Disc :\t" + finer + "\t" + result);
            if (!bestExist) {
                best = finer.toPermutation();
                first = finer.toPermutation();
                bestExist = true;
            } else {
                if (result == Result.BETTER) {
                    best = new Permutation(pi1);
                } else if (result == Result.EQUAL) {
                    pi2 = pi1.multiply(best.invert());
                    if (!checkVertexColors || colorsAutomorphic(pi2)) {
                        group.enter(pi2);
                    }
                }
            }
        } else {
            if (result != Result.WORSE) {
                Set<Integer> blockCopy = finer.copyBlock(firstNonDiscreteCell);
                for (int vertexInBlock = 0; vertexInBlock < vertexCount; vertexInBlock++) {
                    if (blockCopy.contains(vertexInBlock)) {
                        Partition nextPartition = 
                            finer.splitBefore(firstNonDiscreteCell, vertexInBlock);
                        
                        this.refine(group, nextPartition);
                        
                        Permutation permF = new Permutation(vertexCount);
                        Permutation invF = new Permutation(vertexCount);
                        
                        for (int j = 0; j <= firstNonDiscreteCell; j++) {
                            int x = nextPartition.getFirstInCell(j);
                            int i = invF.get(x);
                            int h = permF.get(j);
                            permF.set(j, x);
                            permF.set(i, h);
                            invF.set(h, i);
                            invF.set(x, j);
                        }
                        group.changeBase(permF);
                        for (int j = 0; j < vertexCount; j++) {
                            Permutation g = group.get(firstNonDiscreteCell, j);
                            if (g != null) blockCopy.remove(g.get(vertexInBlock));
                        }
                    }
                }
            }
        }
    }
    
    private boolean colorsAutomorphic(Permutation p) {
        for (int i = 0; i < p.size(); i++) {
            if (sameColor(i, p.get(i))) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Check a permutation to see if it is better, equal, or worse than the 
     * current best.
     * 
     * @param perm the permutation to check
     * @return BETTER, EQUAL, or WORSE
     */
    public Result compareColumnwise(Permutation perm) {
        int m = perm.size();
        for (int i = 1; i < m; i++) {
            for (int j = 0; j < i; j++) {
                int x = isAdjacent(best.get(i), best.get(j));
                int y = isAdjacent(perm.get(i), perm.get(j));
                if (x > y) return Result.WORSE;
                if (x < y) return Result.BETTER;
            }
        }
        return Result.EQUAL;
    }
    
    public Result compareRowwise(Permutation perm) {
        int m = perm.size();
        for (int i = 0; i < m - 1; i++) {
            for (int j = i + 1; j < m; j++) {
                int x = isAdjacent(best.get(i), best.get(j));
                int y = isAdjacent(perm.get(i), perm.get(j));
                if (x > y) return Result.WORSE;
                if (x < y) return Result.BETTER;
            }
        }
        return Result.EQUAL;
    }

    private int isAdjacent(int i, int j) {
        return isConnected(i, j);
//        if (isConnected(i, j)) { 
//            return 1; 
//        } else {
//            return 0;
//        }
    }

}
