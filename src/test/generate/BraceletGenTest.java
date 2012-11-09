package test.generate;

import generate.AtomAugmentingGenerator;
import generate.LabellerMethod;
import generate.ListerMethod;
import generate.ValidatorMethod;
import group.AtomDiscretePartitionRefiner;
import group.Partition;
import handler.DataFormat;
import handler.GenerateHandler;
import handler.PrintStreamStringHandler;

import java.util.List;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import bracelet.AlphabetHandler;
import bracelet.BraceletGenerator;

public class BraceletGenTest {
    
    private static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    public IAtomContainer makeAtomInAtomContainer(String elementSymbol, IChemObjectBuilder builder) {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class,(elementSymbol)));
        return ac;
    }
    
    public IAtomContainer makeAtomContainer(List<String> elementSymbols, IChemObjectBuilder builder) {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        for (String elementSymbol : elementSymbols) {
            ac.addAtom(builder.newInstance(IAtom.class,(elementSymbol)));
        }
        return ac;
    }
    
    @Test
    public void elementPartitionTest() {
        String[] alphabet = new String[] { "C", "N", "O" };
        int[] colorCounts = new int[] { 3, 2, 1 };
        AlphabetHandler alphabetHandler = new AlphabetHandler(alphabet);
        BraceletGenerator braceletGenerator = 
            new BraceletGenerator(colorCounts, alphabetHandler);
        braceletGenerator.genBracelets();
        List<List<String>> convertedBracelets = alphabetHandler.getConvertedBracelets();
        for (List<String> elementSymbols : convertedBracelets) {
            IAtomContainer ac = makeAtomContainer(elementSymbols, builder);
            AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
            Partition p = refiner.getElementPartition(ac);
            System.out.println(elementSymbols + "\t" + p);
        }
    }
    
    @Test
    public void cocoTest() {
        String[] alphabet = new String[] { "C", "O" };
        int[] colorCounts = new int[] { 2, 2 };
        int n = 4;
        int hCount = 4;
        
        AlphabetHandler alphabetHandler = new AlphabetHandler(alphabet);
        BraceletGenerator braceletGenerator = 
            new BraceletGenerator(colorCounts, alphabetHandler);
        braceletGenerator.genBracelets();
        List<List<String>> convertedBracelets = alphabetHandler.getConvertedBracelets();
        GenerateHandler handler = new PrintStreamStringHandler(
                System.out, DataFormat.SMILES, true, false);
        for (List<String> elementSymbols : convertedBracelets) {
            System.out.println(elementSymbols);
            AtomAugmentingGenerator molGen = new AtomAugmentingGenerator(
                    handler, 
                    ListerMethod.FILTER, 
                    LabellerMethod.SIGNATURE,
                    ValidatorMethod.REFINER
            );
            molGen.setElementSymbols(elementSymbols);
            molGen.setHCount(hCount);
            String initialSymbol = elementSymbols.get(0);
            molGen.extend(makeAtomInAtomContainer(initialSymbol, builder), n);
        }
    }
    
    @Test
    public void conTest() {
        String[] alphabet = new String[] { "C", "N", "O" };
        int[] colorCounts = new int[] { 2, 2, 2 };
        int n = 6;
        int hCount = 6;
        
        AlphabetHandler alphabetHandler = new AlphabetHandler(alphabet);
        BraceletGenerator braceletGenerator = 
            new BraceletGenerator(colorCounts, alphabetHandler);
        braceletGenerator.genBracelets();
        List<List<String>> convertedBracelets = alphabetHandler.getConvertedBracelets();
        GenerateHandler handler = new PrintStreamStringHandler(
                System.out, DataFormat.SMILES, true, false);
        for (List<String> elementSymbols : convertedBracelets) {
            System.out.println(elementSymbols);
            AtomAugmentingGenerator molGen = new AtomAugmentingGenerator(
                    handler, ListerMethod.FILTER, LabellerMethod.SIGNATURE, ValidatorMethod.SIGNATURE);
            molGen.setElementSymbols(elementSymbols);
            molGen.setHCount(hCount);
            String initialSymbol = elementSymbols.get(0);
            molGen.extend(makeAtomInAtomContainer(initialSymbol, builder), n);
        }
    }

}
