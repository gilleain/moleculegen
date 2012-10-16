package validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.AtomSignature;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Validate a candidate molecule as a) canonical and b) having the correct number of
 * hydrogens.
 * 
 * With regard to the canonicalization check : this version should  
 * 
 * @author maclean
 *
 */
public class SignatureValidator implements MoleculeValidator {
    
    private int hCount;
    
    public SignatureValidator() {
        hCount = 0;
    }

    @Override
    public boolean isValidMol(IAtomContainer atomContainer, int size) {
        // TODO!
//        System.out.println("validating " + test.AtomContainerPrinter.toString(atomContainer));
        return atomContainer.getAtomCount() == size && hydrogensCorrect(atomContainer);
    }

    private boolean hydrogensCorrect(IAtomContainer atomContainer) {
//        if (hCount < 1) return true;    // XXX
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
            CDKHydrogenAdder adder = 
                CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
            adder.addImplicitHydrogens(atomContainer);
            int actualCount = 0;
            for (IAtom atom : atomContainer.atoms()) {
                actualCount += AtomContainerManipulator.countHydrogens(atomContainer, atom);
                if (actualCount > hCount) {
                    return false;
                }
            }
            return actualCount == hCount;
        } catch (CDKException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isCanonical(IAtomContainer parent, IAtomContainer child, String parentSignature) {
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
    
    @Override
    public void setHCount(int hCount) {
        this.hCount = hCount;
    }

}
