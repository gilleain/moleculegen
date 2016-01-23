package augment.bond;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.openscience.cdk.interfaces.IAtomContainer;

import app.FormulaParser;
import augment.ConstrainedAugmentation;
import augment.ConstrainedAugmentor;
import augment.SaturationCalculator;
import group.AtomDiscretePartitionRefiner;
import group.Partition;
import group.Permutation;
import group.PermutationGroup;

public class BondAugmentor 
    implements ConstrainedAugmentor<IAtomContainer, BondExtension, ElementConstraints> {
   
    private SaturationCalculator saturationCalculator;
    
    private FormulaParser formulaParser;
    
    public BondAugmentor(String elementFormula) {
        this.formulaParser = new FormulaParser(elementFormula);
        this.saturationCalculator = new SaturationCalculator(formulaParser.getElementSymbols());
    }

    @Override
    public List<ConstrainedAugmentation<IAtomContainer, BondExtension, ElementConstraints>> augment(
            ConstrainedAugmentation<IAtomContainer, BondExtension, ElementConstraints> parent) {
        List<ConstrainedAugmentation<IAtomContainer, BondExtension, ElementConstraints>> 
            augmentations = new ArrayList<ConstrainedAugmentation
                                            <IAtomContainer, BondExtension, ElementConstraints>>();
        
        IAtomContainer atomContainer = parent.getBase();
        int atomCount = atomContainer.getAtomCount();
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        PermutationGroup autG = refiner.getAutomorphismGroup(atomContainer);
        int[] saturationCapacity = saturationCalculator.getSaturationCapacity(atomContainer);
        List<Integer> undersaturatedAtoms = 
                saturationCalculator.getUndersaturatedAtoms(atomCount, saturationCapacity);
        
        ElementConstraints constraints = parent.getConstraints();
        for (IndexPair position : getInternalPositions(atomContainer, undersaturatedAtoms, saturationCapacity, autG)) {
            BondExtension extension = new BondExtension(position, null);
            augmentations.add(
                    new BondAugmentation(atomContainer, extension, constraints));
        }
        
        for (String literal : constraints) {
            for (IndexPair position : getExternalPositions(atomCount, undersaturatedAtoms, saturationCapacity, refiner, literal)) {
                BondExtension extension = new BondExtension(position, literal);
                augmentations.add(
                        new BondAugmentation(atomContainer, extension, constraints.minus(literal)));
            }
        }
        
        return augmentations;
    }

    public boolean isComplete(IAtomContainer augmentation) {
        int maxSize = formulaParser.getElementSymbols().size();
        return augmentation.getAtomCount() == maxSize;
    }
    
    private List<IndexPair> getInternalPositions(
            IAtomContainer atomContainer, List<Integer> undersaturatedAtoms, int[] saturationCapacity, PermutationGroup autG) {
        List<IndexPair> positions = new ArrayList<IndexPair>();
        if (undersaturatedAtoms.size() > 1) {
            List<IndexPair> undersaturatedBonds = 
                    saturationCalculator.getUndersaturatedBonds(
                            atomContainer, undersaturatedAtoms, saturationCapacity);
            for (IndexPair pair : undersaturatedBonds) {
                // XXX are k-subsets always ordered?
                if (isMinimal(pair, autG)) {
                    positions.add(pair);
                }
            }
        }
        return positions;
    }
    
    private List<IndexPair> getExternalPositions(
            int atomCount, 
            List<Integer> undersaturatedAtoms, 
            int[] saturationCapacity, 
            AtomDiscretePartitionRefiner refiner,
            String elementSymbol) {
        List<IndexPair> positions = new ArrayList<IndexPair>();
        
        if (atomCount < formulaParser.getElementSymbols().size()) {
            for (int rep : getSingleReps(undersaturatedAtoms, refiner)) {
                // run through the possible bond orders from the maximum down to 1
                int addedSaturation = saturationCalculator.getMaxBondOrder(elementSymbol);
                int max = Math.min(addedSaturation, saturationCapacity[rep]);
                for (int order = max; order > 0; order--) {
                    positions.add(new IndexPair(rep, atomCount, order));
                }
            }
        }
        
        return positions;
    }
    
    private Set<Integer> getSingleReps(List<Integer> undersaturatedAtoms, AtomDiscretePartitionRefiner refiner) {
        Set<Integer> singleReps = new HashSet<Integer>();
        Partition partition = refiner.getAutomorphismPartition();
        for (Integer undersaturatedIndex : undersaturatedAtoms) {
            for (int cellIndex = 0; cellIndex < partition.size(); cellIndex++) {
                SortedSet<Integer> cell = partition.getCell(cellIndex);
                if (cell.contains(undersaturatedIndex)) {
                    singleReps.add(cell.first());
                    break;
                }
            }
        }
        return singleReps;
    }
    
    private boolean isMinimal(IndexPair pair, PermutationGroup autG) {
        String oStr = pair.getStart() + ":" + pair.getEnd();
        for (Permutation p : autG.all()) {
            String pStr = getString(pair, p);
            if (oStr.compareTo(pStr) > 0) {
//                System.out.println("Comparing " + oStr + " to " + pStr);
                return false;
            }
        }
        return true;
    }
    
    private String getString(IndexPair pair, Permutation p) {
        int a = p.get(pair.getStart());
        int b = p.get(pair.getEnd());
        if (a < b) {
            return a + ":" + b;
        } else {
            return b + ":" + a;
        }
    }

}
