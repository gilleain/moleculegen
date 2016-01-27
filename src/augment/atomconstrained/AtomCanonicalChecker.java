package augment.atomconstrained;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import group.AtomDiscretePartitionRefiner;
import group.BondDiscretePartitionRefiner;
import group.Permutation;
import group.PermutationGroup;
import io.AtomContainerPrinter;
import setorbit.BruteForcer;
import setorbit.SetOrbit;
import util.CutCalculator;

public class AtomCanonicalChecker {

    public boolean isCanonical(IAtomContainer augmentedMolecule, AtomExtension augmentation) {
        if (augmentedMolecule.getAtomCount() <= 2) {
            return true;
        }
        
        List<Integer> nonSeparatingAtoms = getNonSeparatingAtoms(augmentedMolecule);
        if (nonSeparatingAtoms.size() == 0) {
            return true;
        }

        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.getAutomorphismGroup(augmentedMolecule);

        BondDiscretePartitionRefiner bondRefiner = new BondDiscretePartitionRefiner();
        try {
            PermutationGroup autBondH = bondRefiner.getAutomorphismGroup(augmentedMolecule);
            Permutation labelling = refiner.getBest().invert();
//            Permutation labelling = refiner.getBest();
            
            int chosen = getChosen(nonSeparatingAtoms, labelling);
            List<Integer> connectedBonds = getConnectedBonds(augmentedMolecule, chosen);
            List<Integer> augmentationIndices = getLastAdded(augmentedMolecule);
            System.out.println("Chosen " + chosen 
                              + " best " + labelling
                              + " connectedBonds " + connectedBonds
                              + " augmentationIndices " + augmentationIndices);
            return inOrbit(connectedBonds, augmentationIndices, autBondH);
        } catch (Exception e) {
            System.out.println(e + "\t" + AtomContainerPrinter.toString(augmentedMolecule));
            return false;
        }
    }
    
    // TODO : combine this method with get non separating atoms
    private int getChosen(List<Integer> nonSeparatingAtoms, Permutation labelling) {
        for (int index = labelling.size() - 1; index >= 0; index--) {
            int label = labelling.get(index);
            if (nonSeparatingAtoms.contains(label)) {
                return label;
            } else {
                continue;
            }
        }
        return -1;  // XXX shouldn't happen...
    }
    
    private List<Integer> getNonSeparatingAtoms(IAtomContainer mol) {
        List<Integer> nonSeparatingVertices = new ArrayList<Integer>(); 
        List<Integer> cutVertices = CutCalculator.getCutVertices(mol);
        for (int index = 0; index < mol.getAtomCount(); index++) {
            if (cutVertices.contains(index)) {
                continue;
            } else {
                nonSeparatingVertices.add(index);
            }
        }
//        System.out.println(io.AtomContainerPrinter.toString(mol) + " " + nonSeparatingVertices);
        return nonSeparatingVertices;
    }

    private List<Integer> getConnectedBonds(IAtomContainer h, int chosenUnderLabelling) {
        List<Integer> connected = new ArrayList<Integer>();
        IAtom chosenAtom = h.getAtom(chosenUnderLabelling);
        for (int bondIndex = 0; bondIndex < h.getBondCount(); bondIndex++) {
            IBond bond = h.getBond(bondIndex); 
            if (bond.contains(chosenAtom)) {
                connected.add(bondIndex);
            }
        }
        //            System.out.println("connected " + Arrays.toString(connected));
        return connected;
    }

    private boolean inOrbit(List<Integer> connectedBonds, List<Integer> augmentation, PermutationGroup autH) {
        //          System.out.println("connected bonds " + connectedBonds + " aug " + augmentation);
        if (connectedBonds.size() == 0) {
            return augmentation.size() == 0;
        }

        // this should not really be necessary...
        if (autH.getSize() == 0) {
            return connectedBonds.equals(augmentation);
        }

        SetOrbit orbit = new BruteForcer().getInOrbit(connectedBonds, autH);
        for (List<Integer> subset : orbit) {
            System.out.println("subset "  + subset);
            if (subset.equals(augmentation)) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> getLastAdded(IAtomContainer augmentedMolecule) {
        int last = augmentedMolecule.getAtomCount() - 1;
        List<Integer> bondIndices = new ArrayList<Integer>();
        for (int bondIndex = 0; bondIndex < augmentedMolecule.getBondCount(); bondIndex++) {
            IBond bond = augmentedMolecule.getBond(bondIndex);
            IAtom a0 = bond.getAtom(0);
            IAtom a1 = bond.getAtom(1);
            int a0n = augmentedMolecule.getAtomNumber(a0);
            int a1n = augmentedMolecule.getAtomNumber(a1);
            if (a0n == last || a1n == last) {
                bondIndices.add(bondIndex);
            }
        }
        return bondIndices;
    }

}
