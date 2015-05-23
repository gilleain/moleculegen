package branch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;

public class DuplicateHandler implements Handler {
    
    private Map<String, List<IAtomContainer>> dupMap;
    
    public DuplicateHandler() {
        this.dupMap = new HashMap<String, List<IAtomContainer>>();
    }

    @Override
    public void handle(IAtomContainer atomContainer) {
        String sig = new MoleculeSignature(atomContainer).toCanonicalString();
        List<IAtomContainer> dups;
        if (dupMap.containsKey(sig)) {
            dups = dupMap.get(sig);
        } else {
            dups = new ArrayList<IAtomContainer>();
            dupMap.put(sig, dups);
        }
        dups.add(atomContainer);
    }
    
    public Map<String, List<IAtomContainer>> getDupMap() {
        return dupMap;
    }

    @Override
    public void finish() {
        // no-op
    }

}
