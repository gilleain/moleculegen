package app;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Start from a list of element symbols only.
 * 
 * @author maclean
 *
 */
public class FromScratchAugmentor {
    
    private static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     * @param generator the augmenting generator to use
     * @param heavyAtomCount the count of heavy atoms
     */
    public static void run(AugmentingGenerator generator, int heavyAtomCount) {
        List<String> symbols = generator.getElementSymbols();
        String firstSymbol = symbols.get(0);
        IAtomContainer startingAtom = makeAtomInAtomContainer(firstSymbol, builder);
        generator.extend(startingAtom, heavyAtomCount);
    }
    
    private static IAtomContainer makeAtomInAtomContainer(String elementSymbol, IChemObjectBuilder builder) {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class,(elementSymbol)));
        return ac;
    }

}
