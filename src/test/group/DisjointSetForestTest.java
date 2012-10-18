package test.group;

import group.AtomDiscretePartitionRefiner;
import group.DisjointSetForest;
import group.OrbitLister;
import group.Partition;
import group.Permutation;
import group.PermutationGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import test.AtomContainerPrinter;

public class DisjointSetForestTest {
    
    public void sort(IAtomContainer ac) {
        for (IBond bond : ac.bonds()) {
            IAtom a0 = bond.getAtom(0);
            IAtom a1 = bond.getAtom(1);
            if (ac.getAtomNumber(a0) > ac.getAtomNumber(a1)) {
                bond.setAtom(a1, 0);
                bond.setAtom(a0, 1);
            }
        }
    }
    
    public Partition getAutPartitionOld(int n, PermutationGroup group) {
        Partition partition = new Partition();
        boolean[] inOrbit = new boolean[n];
        List<Permutation> permutations = group.all();
        int cellIndex = 0;
        for (int i = 0; i < n; i++) {
            if (inOrbit[i]) {
                continue;
            } else {
                inOrbit[i] = true;
                partition.addSingletonCell(i);
                for (Permutation p : permutations) {
                    int x = p.get(i);
                    while (x != i) {
                        inOrbit[x] = true;
                        partition.addToCell(cellIndex, x);
                        x = p.get(x);
                    }
                }
                cellIndex++;
            }
        }
        return partition;
    }
    
    public Partition getAutPartition(int n, PermutationGroup group) {
        boolean[] inOrbit = new boolean[n];
        List<Permutation> permutations = group.all();
        int cellIndex = 0;
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
                    cellIndex++;
                }
            }
//            System.out.println(p + "\t" + set);
            if (inOrbitCount == n) {
                break;
            }
        }
        
        // convert to a partition
        Partition partition = new Partition();
        for (int[] set : forest.getSets()) {
            partition.addCell(set);
        }
        return partition;
    }

    @Test
    public void symNTest() {
        int size = 4;
        PermutationGroup sym4 = PermutationGroup.makeSymN(size);
        System.out.println(getAutPartition(size, sym4));
        int kvalue = 2;
        OrbitLister lister = new OrbitLister();
        int[][] orbitList = lister.getOrbits(kvalue, size, sym4);
        System.out.println(Arrays.deepToString(orbitList));
    }
    
    @Test
    public void molAutomorphismTest() throws InvalidSmilesException {
        IAtomContainer ac = new SmilesParser(
                SilentChemObjectBuilder.getInstance()).parseSmiles("C1=CCCC1");
        sort(ac);
        AtomContainerPrinter.print(ac);
        AtomDiscretePartitionRefiner refiner = 
            new AtomDiscretePartitionRefiner(false, true, true, false);
        PermutationGroup group = refiner.getAutomorphismGroup(ac);
        System.out.println("Group order = " + group.order());
        
        Partition p = getAutPartition(ac.getAtomCount(), group);
        System.out.println(p);
//        OrbitLister lister = new OrbitLister();
//        int k = 1;
//        int[][] orbits = lister.getOrbits(k, ac.getAtomCount(), group);
//        System.out.println(Arrays.deepToString(orbits));
    }
    
    @Test
    public void nautyTest() {
        List<Permutation> generators = new ArrayList<Permutation>();
        generators.add(new Permutation(0, 1, 2, 5, 4, 3, 6, 7));
        generators.add(new Permutation(0, 1, 7, 3, 6, 5, 4, 2));
        OrbitLister lister = new OrbitLister();
        int kvalue = 1;
        int[][] orbitList = lister.getOrbits(kvalue, 8, generators);
        System.out.println(Arrays.deepToString(orbitList));
    }
    
    @Test
    public void cagesPage228Test() {
        List<Permutation> generators = new ArrayList<Permutation>();
        generators.add(new Permutation(0, 2, 3, 1, 5, 6, 4));
        generators.add(new Permutation(0, 2, 1, 3, 4, 6, 5));
        OrbitLister lister = new OrbitLister();
        int kvalue = 2;
        int[][] orbitList = lister.getOrbits(kvalue, 7, generators);
        System.out.println(Arrays.deepToString(orbitList));
        int[][] orbitList2 = lister.getOrbits(kvalue, 7, new PermutationGroup(7, generators));
        System.out.println(Arrays.deepToString(orbitList2));
    }

    @Test
    public void join() {
        int n = 10;
        DisjointSetForest set = new DisjointSetForest(n);
        System.out.println(set);
        set.makeUnion(0, 1);
        System.out.println(set);
        set.makeUnion(2, 3);
        System.out.println(set);
        set.makeUnion(4, 5);
        System.out.println(set);
    }

}
