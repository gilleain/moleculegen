package handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;

public class DuplicateCountingHandler implements GenerateHandler {
    
    private Map<String, IAtomContainer> sigMap;
    
    private Map<String, List<IAtomContainer>> dupMap;
    
    public DuplicateCountingHandler() {
        sigMap = new HashMap<String, IAtomContainer>();
        dupMap = new HashMap<String, List<IAtomContainer>>();
    }

    @Override
    public void handle(IAtomContainer parent, IAtomContainer child) {
        String canonicalSignature = new MoleculeSignature(child).toCanonicalString();
        if (sigMap.containsKey(canonicalSignature)) {
            if (dupMap.containsKey(canonicalSignature)) {
                dupMap.get(canonicalSignature).add(child);
            } else {
                IAtomContainer original = sigMap.get(canonicalSignature);
                List<IAtomContainer> dups = new ArrayList<IAtomContainer>();
                dups.add(original);
                dups.add(child);
                dupMap.put(canonicalSignature, dups);
            }
        } else {
            sigMap.put(canonicalSignature, child);
        }
    }
    
    public Map<String, List<IAtomContainer>> getDupMap() {
        return dupMap;
    }

    @Override
    public void finish() {
        
    }

}
