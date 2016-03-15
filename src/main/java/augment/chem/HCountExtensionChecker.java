package augment.chem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import augment.constraints.ElementConstraints;

/**
 * Checks a partial structure to see if it could possibly be extended to   
 * 
 * @author maclean
 *
 */
public class HCountExtensionChecker {
    
    private final int targetHCount;
    
    private final BondOrderMaps bondOrderMaps;

    public HCountExtensionChecker(int targetHCount) {
        this.targetHCount = targetHCount;
        this.bondOrderMaps = new BondOrderMaps();
    }
    
    public boolean canExtend(IAtomContainer partial, ElementConstraints elementConstraints) {
        if (partial.getAtomCount() < 2) return true;    // FIXME
        
        int currentHCount = 0;
        Map<IAtom, Integer> hCapMap = new HashMap<IAtom, Integer>();
        for (IBond bond : partial.bonds()) {
            IAtom atom0 = bond.getAtom(0);
            IAtom atom1 = bond.getAtom(1);
            int order = bond.getOrder().numeric();
            reduceCount(atom0, hCapMap, order);
            reduceCount(atom1, hCapMap, order);
        }
        
        for (IAtom atom : hCapMap.keySet()) {
            currentHCount += hCapMap.get(atom);
        }
        
        int minCount = currentHCount;
        int maxCount = currentHCount;
        for (String element : elementConstraints) {
            for (int count = 0; count < elementConstraints.getCount(element); count++) {
                int bos = bondOrderMaps.getMaxBondOrderSum(element);
                minCount -= bos;
                maxCount += bos - 2;    // -1 hydrogen for each end of an added single bond
            }
        }
        
        // hcount cannot be negative...
        minCount = Math.max(0, minCount);
        
        boolean accept = minCount <= targetHCount && targetHCount <= maxCount;
        
//        print(
//                io.AtomContainerPrinter.toString(partial),
//                currentHCount,
//                minCount,
//                maxCount,
//                accept
//        );
        
        return accept;
    }
    
    private void reduceCount(IAtom atom, Map<IAtom, Integer> hCapMap, int order) {
        if (hCapMap.containsKey(atom)) {
            hCapMap.put(atom, hCapMap.get(atom) - order);
        } else {
            hCapMap.put(atom, bondOrderMaps.getMaxBondOrderSum(atom.getSymbol()) - order);
        }
    }
    
    private void print(Object... stuff) {
        String[] percents = new String[stuff.length];
        Arrays.fill(percents, "%s");
        String formatString = String.join("\t", percents);
//        System.out.println(String.format(formatString, stuff));
    }

}
