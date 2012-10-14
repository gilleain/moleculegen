package validate;

import java.util.Arrays;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import test.AtomContainerPrinter;

public class SimpleValidator implements MoleculeValidator {
    
    private int hCount;
    
    public SimpleValidator() {
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

    @Override
    public boolean isCanonical(IAtomContainer parent, IAtomContainer child, String parentSignature) {
        MoleculeSignature molSig = new MoleculeSignature(child);
        int[] labels = molSig.getCanonicalLabels();
        int size = labels.length - 1;
//        int vertexToDelete = labels[size];
        int vertexToDelete = find(labels, size);

        IAtomContainer canonicalParent = null;
        try { canonicalParent = (IAtomContainer) child.clone(); } catch (Exception e) {} 

        IAtom atomToDelete = canonicalParent.getAtom(vertexToDelete);
        canonicalParent.removeAtomAndConnectedElectronContainers(atomToDelete);
        MoleculeSignature canonicalParentSig = new MoleculeSignature(canonicalParent);
        String canonParentString = canonicalParentSig.toCanonicalString(); 
        boolean canon = canonParentString.equals(parentSignature);
        boolean debug = false;
//        boolean debug = true;
        if (canon) {
            if (debug) {
                System.out.println(canonParentString 
                        + " == " + parentSignature
                        + " -> " + AtomContainerPrinter.toString(child)
                        + " l = " + Arrays.toString(labels)
                        + " " + vertexToDelete);
            }
        } else {
            if (debug) {
                System.out.println(canonParentString 
                        + " != " + parentSignature
                        + " -> " + AtomContainerPrinter.toString(child)
                        + " l = " + Arrays.toString(labels)
                        + " " + vertexToDelete);
            }
        }
        return canon;
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
