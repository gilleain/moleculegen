package appbranch.augment.bond;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.openscience.cdk.interfaces.IAtomContainer;

import appbranch.FormulaParser;
import appbranch.augment.Augmentation;
import appbranch.augment.Augmentor;
import appbranch.augment.ExtensionSource;
import appbranch.augment.SaturationCalculator;
import group.AtomDiscretePartitionRefiner;
import group.Partition;
import group.Permutation;
import group.PermutationGroup;

public class BondAugmentor implements Augmentor<IAtomContainer, BondExtension> {
    
    private ExtensionSource<IndexPair, ElementPair> extensionSource;
    
    private SaturationCalculator saturationCalculator;
    
    private FormulaParser formulaParser;
    
    public BondAugmentor(String elementFormula) {
        formulaParser = new FormulaParser(elementFormula);
        this.extensionSource = new ElementSymbolPairSource(formulaParser);
        this.saturationCalculator = new SaturationCalculator(formulaParser.getElementSymbols());
    }
    
    public boolean isComplete(Augmentation<IAtomContainer, BondExtension> augmentation) {
        int maxSize = formulaParser.getElementSymbols().size();
        return augmentation.getBase().getAtomCount() == maxSize;
    }

    @Override
    public List<Augmentation<IAtomContainer, BondExtension>> augment(
            Augmentation<IAtomContainer, BondExtension> parent) {
        IAtomContainer atomContainer = parent.getBase();
        List<Augmentation<IAtomContainer, BondExtension>> augmentations = 
                new ArrayList<Augmentation<IAtomContainer, BondExtension>>();
        for (IndexPair position : getPositions(atomContainer)) {
            ElementPair elementPair = extensionSource.getNext(position);
            augmentations.add(
                    new BondAugmentation(atomContainer, new BondExtension(position, elementPair)));
        }
        return augmentations;
    }

    private List<IndexPair> getPositions(IAtomContainer atomContainer) {
        List<IndexPair> positions = new ArrayList<IndexPair>();
        
        // TODO : only calculate these when necessary (number of unsaturated atoms > 2?)
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        PermutationGroup autG = refiner.getAutomorphismGroup(atomContainer);
        
        int atomCount = atomContainer.getAtomCount();
        int[] saturationCapacity = saturationCalculator.getSaturationCapacity(atomContainer);
        
        // Get the external bonds if there is capacity
        List<Integer> undersaturatedAtoms = saturationCalculator.getUndersaturatedAtoms(atomCount, saturationCapacity);
        if (atomCount < formulaParser.getElementSymbols().size()) {
            for (int rep : getSingleReps(undersaturatedAtoms, refiner)) {
                // run through the possible bond orders from the maximum down to 1
                int max = Math.min(3, saturationCapacity[rep]); // XXX carbon only!!!
                for (int order = max; order > 0; order--) {
                    positions.add(new IndexPair(rep, atomCount, order));
                }
            }
        }
        
        // Get the internal bonds
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
        
        // Add a disconnected atom
        if (atomCount < formulaParser.getElementSymbols().size()) {
            positions.add(new IndexPair(0, atomCount, 0));    // nasty hack...
        }
        
//        System.out.println(io.AtomContainerPrinter.toString(atomContainer) + " + " + positions);
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
