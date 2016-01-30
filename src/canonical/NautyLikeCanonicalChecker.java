package canonical;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import augment.Augmentation;
import augment.atom.AtomExtension;
import group.BondDiscretePartitionRefiner;
import group.Permutation;
import group.PermutationGroup;
import setorbit.BruteForcer;
import setorbit.SetOrbit;

/**
 * Canonical check using the procedure described in the nAUTy User Guide.
 * 
 * For each edge color (bond order), make a layer in a new graph (molecule!) and make edges
 * between vertices in these layers according to the colors of edges in the original. 
 * 
 * @author maclean
 *
 */
public class NautyLikeCanonicalChecker implements CanonicalChecker<IAtomContainer, AtomExtension> {
    
    @Override
    public boolean isCanonical(Augmentation<IAtomContainer, AtomExtension> augmentation) {
        IAtomContainer augmentedMolecule = augmentation.getBase();
        return NautyLikeCanonicalChecker.isCanonical(augmentedMolecule, getLastAdded(augmentedMolecule));
    }

    public static boolean isCanonical(IAtomContainer atomContainer, List<Integer> augmentedBonds) {
        IAtomContainer transformedContainer = transform(atomContainer);
        Set<Integer> transformedAugmentedBonds = transformBonds(atomContainer, transformedContainer, augmentedBonds);
        BondDiscretePartitionRefiner bondRefiner = new BondDiscretePartitionRefiner();
        PermutationGroup autGE = bondRefiner.getAutomorphismGroup(transformedContainer);
        Permutation p = bondRefiner.getBest();
        Set<Integer> chosen = chosen(transformedContainer, p);
        return inOrbit(transformedAugmentedBonds, chosen, autGE);
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
    
    private static Set<Integer> transformBonds(
            IAtomContainer atomContainer, IAtomContainer transformedContainer, List<Integer> augmentedBonds) {
        Set<Integer> chosen = new HashSet<Integer>();
        
        // bit of a hack!
        int numberOfLayers = transformedContainer.getAtomCount() / atomContainer.getAtomCount();
        
        for (int bondIndex : augmentedBonds) {
            IBond bond = atomContainer.getBond(bondIndex);
            int order = bond.getOrder().numeric();
            int a0i = transformedIndex(atomContainer, bond.getAtom(0), numberOfLayers, order);
            int a1i = transformedIndex(atomContainer, bond.getAtom(1), numberOfLayers, order);
            IAtom tA0 = transformedContainer.getAtom(a0i);
            IAtom tA1 = transformedContainer.getAtom(a1i);
            chosen.add(transformedContainer.getBondNumber(tA0, tA1));
        }
        return chosen;
    }
    
    private static boolean inOrbit(Set<Integer> augmentedBonds, Set<Integer> chosen, PermutationGroup g) {
        List<Integer> setList = new ArrayList<Integer>();
        for (int a : augmentedBonds) { setList.add(a); }
        SetOrbit orbit = new BruteForcer().getInOrbit(setList, g);
        for (List<Integer> o : orbit) {
            if (o.equals(chosen)) {
                return true;
            }
        }
        return false;
    }
    
    private static Set<Integer> chosen(IAtomContainer ac, Permutation p) {
        Set<Integer> chosen = new HashSet<Integer>();
        
        return chosen;
    }
    
    public static IAtomContainer transform(IAtomContainer atomContainer) {
        IChemObjectBuilder builder = atomContainer.getBuilder();
        IAtomContainer transformed = builder.newInstance(IAtomContainer.class);
        
        int numberOfLayers = 1;
        for (IBond bond : atomContainer.bonds()) {
            int o = bond.getOrder().numeric();
            if (o > numberOfLayers) {
                numberOfLayers = o; 
            }
        }
        
        for (IAtom atom : atomContainer.atoms()) {
            IAtom prev = null;
            for (int layerIndex = 0; layerIndex < numberOfLayers; layerIndex++) {
                IAtom newAtom = builder.newInstance(IAtom.class, atom.getSymbol());
                transformed.addAtom(newAtom);
                if (prev != null) {
                    transformed.addBond(builder.newInstance(IBond.class, prev, newAtom));
                }
                prev = newAtom;
            }
        }
        
        for (IBond bond : atomContainer.bonds()) {
            int o = bond.getOrder().numeric();
            int a0t = transformedIndex(atomContainer, bond.getAtom(0), numberOfLayers, o);
            int a1t = transformedIndex(atomContainer, bond.getAtom(1), numberOfLayers, o);
            transformed.addBond(a0t, a1t, IBond.Order.SINGLE);
        }
        
        return transformed;
    }
    
    private static int transformedIndex(IAtomContainer ac, IAtom atom, int numberOfLayers, int order) {
        return transformedIndex(ac, ac.getAtomNumber(atom), numberOfLayers, order);
    }
    
    private static int transformedIndex(IAtomContainer ac, int originalIndex, int numberOfLayers, int order) {
//        System.out.println(String.format("(%s * %s) + %s)", originalIndex, numberOfLayers, order - 1));
        return (originalIndex * numberOfLayers) + (order - 1);
    }

}
