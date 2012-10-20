package validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.AtomSignature;
import org.openscience.cdk.signature.MoleculeSignature;

/**
 *  Validate a molecule as a canonical augmentation.
 *   
 * @author maclean
 *
 */
public class SignatureCanonicalValidator implements CanonicalValidator {
    
    public boolean isCanonical(IAtomContainer child) {
        MoleculeSignature molSig = new MoleculeSignature(child);
        
        // for speed reasons, find both the labels and the orbits at the same time
        Map<String, List<Integer>> orbits = new HashMap<String, List<Integer>>();
        String maxSig = null;
        AtomSignature maxAtomSig = null;
        for (int i = 0; i < child.getAtomCount(); i++) {
            AtomSignature sigForI = (AtomSignature) molSig.signatureForVertex(i);
            String sigStringForI = sigForI.toCanonicalString();
            if (maxSig == null || sigStringForI.compareTo(maxSig) < 0) {
                maxSig = sigStringForI;
                maxAtomSig = sigForI;
            }
            if (orbits.containsKey(sigStringForI)) {
                orbits.get(sigStringForI).add(i);
            } else {
                List<Integer> orbit = new ArrayList<Integer>();
                orbits.put(sigStringForI, orbit);
                orbit.add(i);
            }
        }
        // TODO : implement a method in AbstVertSig to get the inverse labeling
        int[] labels = maxAtomSig.getCanonicalLabelling(child.getAtomCount());
        
//        for (Orbit o : orbits) { System.out.println(o); }
        int size = labels.length - 1;
        int del = find(labels, size);
//        System.out.println("found " + del + " in " + java.util.Arrays.toString(labels));
        return del == size || inOrbit(size, del, orbits.values());
    }
    
    private boolean inOrbit(int i, int j, Collection<List<Integer>> orbits) {
        for (List<Integer> o : orbits) {
            if (o.contains(i) && o.contains(j)) {
                return true;
            }
        }
        return false;
    }
    
    private int find(int[] labels, int j) {
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] == j) return i;
        }
        return -1;
    }

}
