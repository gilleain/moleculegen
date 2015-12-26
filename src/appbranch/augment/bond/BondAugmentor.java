package appbranch.augment.bond;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import appbranch.FormulaParser;
import appbranch.augment.Augmentation;
import appbranch.augment.Augmentor;
import appbranch.augment.ExtensionSource;
import appbranch.augment.SaturationCalculator;

public class BondAugmentor implements Augmentor<IAtomContainer, BondExtension> {
    
    private ExtensionSource<IndexPair, ElementPair> extensionSource;
    
    private SaturationCalculator saturationCalculator;
    
    public BondAugmentor(String elementFormula) {
        FormulaParser formulaParser = new FormulaParser(elementFormula);
        this.extensionSource = new ElementSymbolPairSource(formulaParser);
        this.saturationCalculator = new SaturationCalculator(formulaParser.getElementSymbols());
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
        int atomCount = atomContainer.getAtomCount();
        int[] saturationCapacity = saturationCalculator.getSaturationCapacity(atomContainer);
        List<Integer> baseSet = saturationCalculator.getUndersaturatedSet(atomCount, saturationCapacity);
        
        return null;
    }

}
