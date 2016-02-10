package augment.constraints;

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.signature.AtomSignature;

public class AtomSignatureConstraints {
    
    private Map<AtomSignature, Integer> signatureCounts;
    
    public AtomSignatureConstraints(Map<AtomSignature, Integer> signatureCounts) {
        this.signatureCounts = signatureCounts;
    }
    
    public AtomSignatureConstraints(Map<AtomSignature, Integer> signatureCounts, AtomSignature toRemove) {
        this.signatureCounts = new HashMap<AtomSignature, Integer>();
        for (AtomSignature atomSignature : signatureCounts.keySet()) {
            int count;
            if (atomSignature.equals(toRemove)) {
                count = signatureCounts.get(atomSignature) - 1;
            } else {
                count = signatureCounts.get(atomSignature);
            }
            if (count > 0) {
                this.signatureCounts.put(atomSignature, count);
            }
        }
    }

}
