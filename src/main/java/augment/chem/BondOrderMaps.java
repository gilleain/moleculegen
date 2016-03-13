package augment.chem;

import java.util.HashMap;
import java.util.Map;

public class BondOrderMaps {
    
    /**
     * TODO : this is a very crude method
     * The max BOS is the maximum sum of bond orders of the bonds 
     * attached to an atom of this element type. eg if maxBOS = 4, 
     * then the atom can have any of {{4}, {3, 1}, {2, 2}, {2, 1, 1}, ...} 
     */
    private final Map<String, Integer> maxBondOrderSumMap;
    
    /**
     * TODO : this is a very crude method
     * The max bond order is the maximum order of any bond attached.
     */
    private final Map<String, Integer> maxBondOrderMap;
    
    public BondOrderMaps() {
        maxBondOrderSumMap = new HashMap<String, Integer>();
        maxBondOrderSumMap.put("C", 4);
        maxBondOrderSumMap.put("O", 2);
        maxBondOrderSumMap.put("N", 3);
//        maxBondOrderSumMap.put("N", 5); XXX pentavalent N is rare
        maxBondOrderSumMap.put("Ag", 4);
        maxBondOrderSumMap.put("As", 4);
        maxBondOrderSumMap.put("Fe", 5);
        maxBondOrderSumMap.put("S", 6);
        maxBondOrderSumMap.put("P", 5);
        maxBondOrderSumMap.put("Br", 1);
        maxBondOrderSumMap.put("F", 1);
        maxBondOrderSumMap.put("I", 1);
        maxBondOrderSumMap.put("Cl", 1);
        
        maxBondOrderMap = new HashMap<String, Integer>();
        maxBondOrderMap.put("C", 3);
        maxBondOrderMap.put("O", 2);
        maxBondOrderMap.put("N", 3);
        maxBondOrderMap.put("Ag", 1);
        maxBondOrderMap.put("As", 1);
        maxBondOrderMap.put("Fe", 1);
        maxBondOrderMap.put("S", 2);
        maxBondOrderMap.put("P", 2);
        maxBondOrderMap.put("Br", 1);
        maxBondOrderMap.put("F", 1);
        maxBondOrderMap.put("I", 1);
        maxBondOrderMap.put("Cl", 1);
    }

    public int getMaxBondOrderSum(String elementSymbol) {
        return maxBondOrderSumMap.get(elementSymbol);
    }
    
    public int getMaxBondOrder(String elementSymbol) {
        return maxBondOrderMap.get(elementSymbol);
    }

}
