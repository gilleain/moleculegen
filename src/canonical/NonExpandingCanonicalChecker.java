package canonical;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import augment.Augmentation;
import augment.atom.AtomExtension;
import group.AtomDiscretePartitionRefiner;
import group.BondDiscretePartitionRefiner;
import group.Permutation;
import group.PermutationGroup;
import io.AtomContainerPrinter;
import setorbit.BruteForcer;
import setorbit.SetOrbit;

public class NonExpandingCanonicalChecker implements CanonicalChecker<IAtomContainer, AtomExtension> {

    @Override
    public boolean isCanonical(Augmentation<IAtomContainer, AtomExtension> augmentation) {
        IAtomContainer augmentedMolecule = augmentation.getBase();
        if (augmentedMolecule.getAtomCount() <= 2 || augmentedMolecule.getBondCount() == 0) {
            return true;
        }
        
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.getAutomorphismGroup(augmentedMolecule);
        
        BondDiscretePartitionRefiner bondRefiner = new BondDiscretePartitionRefiner();
        try {
            PermutationGroup autBondH = bondRefiner.getAutomorphismGroup(augmentedMolecule);
//            System.out.println("Bond labelling " + bondRefiner.getBest());
        
//        Permutation labelling = refiner.getBest().invert();
            Permutation labelling = refiner.getBest();
//        System.out.println("Atom labelling " + labelling);
            List<Integer> connectedBonds = getConnectedBonds(augmentedMolecule, labelling);
            List<Integer> augmentationIndices = getLastAdded(augmentedMolecule);
            return inOrbit(connectedBonds, augmentationIndices, autBondH);
        } catch (Exception e) {
            System.out.println(e + "\t" + AtomContainerPrinter.toString(augmentedMolecule));
            return false;
        }
    }
    
    private List<Integer> getConnectedBonds(IAtomContainer h, Permutation labelling) {
        List<Integer> connected = new ArrayList<Integer>();
        int chosen = labelling.get(h.getAtomCount() - 1);
//        System.out.println("chosen " + chosen + " under labelling " + labelling);
        IAtom chosenAtom = h.getAtom(chosen);
        for (int bondIndex = 0; bondIndex < h.getBondCount(); bondIndex++) {
            IBond bond = h.getBond(bondIndex); 
            if (bond.contains(chosenAtom)) {
                connected.add(bondIndex);
            }
        }
//        System.out.println("connected " + Arrays.toString(connected));
        return connected;
    }
    
    private boolean inOrbit(List<Integer> connectedBonds, List<Integer> augmentation, PermutationGroup autH) {
//      System.out.println("connected bonds " + connectedBonds + " aug " + augmentation);
      if (connectedBonds.size() == 0) {
          return augmentation.size() == 0;
      }
      
      // this should not really be necessary...
      if (autH.getSize() == 0) {
          return connectedBonds.equals(augmentation);
      }
      
      SetOrbit orbit = new BruteForcer().getInOrbit(connectedBonds, autH);
      for (List<Integer> subset : orbit) {
//          System.out.println("subset "  + subset);
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
