package augment.chem;

import org.openscience.cdk.interfaces.IAtomContainer;

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
        
        
        return true;
    }

}
