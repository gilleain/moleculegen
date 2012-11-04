package group;

import java.util.List;
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
    
    private boolean isVertexColorPreserving;
    
    public AbstractDiscretePartitionRefiner() {
        this(false);
    }
    
    public AbstractDiscretePartitionRefiner(boolean isVertexColorPreserving) {
        this.bestExist = false;
        this.best = null;
        this.equitableRefiner = null;
        this.isVertexColorPreserving = isVertexColorPreserving;
    }
    
    public abstract int getVertexCount();
    
    public abstract int getConnectivity(int i, int j);
    
    public void setup(PermutationGroup group, IEquitablePartitionRefiner refiner) {
        this.bestExist = false;
        this.best = null;
        this.group = group;
        this.equitableRefiner = refiner;
    }
    
    public boolean firstIsIdentity() {
        return this.first.isIdentity();
    }
    
    /**
     * The automorphism partition is a partition of the elements of the group.
     * 
     * @return a partition of the elements of group 
     */
    public Partition getAutomorphismPartition() {
        int n = group.getSize();
        boolean[] inOrbit = new boolean[n];
        List<Permutation> permutations = group.all();
        DisjointSetForest forest = new DisjointSetForest(n);
        int inOrbitCount = 0;
        for (Permutation p : permutations) {
            for (int i = 0; i < n; i++) {
                if (inOrbit[i]) {
                    continue;
                } else {
                    int x = p.get(i);
                    while (x != i) {
                        if (!inOrbit[x]) {
                            inOrbitCount++;
                            inOrbit[x] = true;
                            forest.makeUnion(i, x);
                        }
                        x = p.get(x);
                    }
                }
            }
            if (inOrbitCount == n) {
                break;
            }
        }
        
        // convert to a partition
        Partition partition = new Partition();
        for (int[] set : forest.getSets()) {
            partition.addCell(set);
        }
        
        // necessary for comparison by string
        partition.order();  
        return partition;
    }
    
    public String getHalfMatrixString(Permutation p) {
        String hms = "";
        int n = p.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                int connectivity = getConnectivity(p.get(i), p.get(j)); 
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
        Permutation pi2 = new Permutation(vertexCount); // TODO : move this down to local block
        
        Result result = Result.BETTER;
        if (bestExist) {
            finer.setAsPermutation(pi1, firstNonDiscreteCell);
            result = compareRowwise(pi1);
        }
        
        // partition is discrete
        if (finer.size() == vertexCount && (!isVertexColorPreserving || colorsPreserved(pi1))) {    
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
                    group.enter(pi2);
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
    
    /**
     * Check a permutation to see if it is better, equal, or worse than the 
     * current best.
     * 
     * @param perm the permutation to check
     * @return BETTER, EQUAL, or WORSE
     */
    public Result compareRowwise(Permutation perm) {
        int m = perm.size();
        for (int i = 0; i < m - 1; i++) {
            for (int j = i + 1; j < m; j++) {
                int x = getConnectivity(best.get(i), best.get(j));
                int y = getConnectivity(perm.get(i), perm.get(j));
                if (x > y) return Result.WORSE;
                if (x < y) return Result.BETTER;
            }
        }
        return Result.EQUAL;
    }
    
    public abstract boolean colorsPreserved(Permutation p);
}
