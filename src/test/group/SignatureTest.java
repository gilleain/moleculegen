package test.group;

import java.util.Arrays;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import test.AtomContainerPrinter;

public class SignatureTest {
    
    public IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    @Test
    public void cloneTest() throws CloneNotSupportedException {
        String acp = "C0C1C2 0:1(2),0:2(1)";
        IAtomContainer original = AtomContainerPrinter.fromString(acp, builder);
        MoleculeSignature molSig = new MoleculeSignature(original);
        int[] labels = molSig.getCanonicalLabels();
        System.out.println(Arrays.toString(labels));
        IAtomContainer cloned = (IAtomContainer) original.clone();
        int[] cloneLabels = new MoleculeSignature(cloned).getCanonicalLabels();
        System.out.println(Arrays.toString(cloneLabels));
    }
}
