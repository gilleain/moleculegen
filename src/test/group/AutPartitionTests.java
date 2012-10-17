package test.group;

import group.CDKDiscretePartitionRefiner;
import group.DisjointSetForest;
import group.Partition;
import group.Permutation;
import group.SSPermutationGroup;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.signature.Orbit;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import test.AtomContainerPrinter;

public class AutPartitionTests {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
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
    
    public void testFile(String filename) throws IOException {
        IIteratingChemObjectReader<IAtomContainer> reader = 
            new IteratingSMILESReader(new FileReader(filename), builder);
        
        while (reader.hasNext()) {
            IAtomContainer ac = reader.next();
            sort(ac);
            MoleculeSignature molSig = new MoleculeSignature(ac);
            List<Orbit> signatureOrbits = molSig.calculateOrbits();
            CDKDiscretePartitionRefiner refiner = 
                new CDKDiscretePartitionRefiner(false, true, false, false);
            int n = ac.getAtomCount();
            SSPermutationGroup group = refiner.getAutomorphismGroup(ac, getElementPartition(ac));
            Partition autPartition = getAutPartition(n, group);
            String pS = simplify(signatureOrbits).toString();
            String aS = autPartition.toString();
            if (!pS.equals(aS)) {
                String acp = AtomContainerPrinter.toString(ac);
                System.out.println(pS + "\t" + aS + "\t" + group.order() + "\t" + acp);
            }
        }
        reader.close();
    }
    
    private Partition getElementPartition(IAtomContainer atomContainer) {
        Map<String, List<Integer>> elementMap = new HashMap<String, List<Integer>>();
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            String elementSymbol = atomContainer.getAtom(i).getSymbol();
            List<Integer> cell;
            if (elementMap.containsKey(elementSymbol)) {
                cell = elementMap.get(elementSymbol);
            } else {
                cell = new ArrayList<Integer>();
                elementMap.put(elementSymbol, cell);
            }
            cell.add(i);
        }
        Partition p = new Partition();
        for (String e : elementMap.keySet()) {
            p.addCell(elementMap.get(e));
        }
        p.order();
        return p;
    }
    
    private Partition simplify(List<Orbit> orbits) {
        Partition p = new Partition();
        for (Orbit o : orbits) {
            p.addCell(o.getAtomIndices());
        }
        return p;
    }
    
    public Partition getAutPartition(int n, SSPermutationGroup group) {
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
    public void testC4H6O() throws IOException {
        testFile("output/C4H6O.smi");
    }
    
    @Test
    public void testC6H8() throws IOException {
        testFile("output/C6H8.smi");
    }

}
