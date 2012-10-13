package test.generate;

import generate.AtomSymmetricChildLister;
import group.Permutation;
import group.SSPermutationGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import test.AtomContainerPrinter;

public class AtomSymmetricChildListerTest extends BaseTest {
    
    public IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    public IAtomContainer make3Star() {
        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        atomContainer.addAtom(builder.newInstance(IAtom.class, ("C")));
        atomContainer.addAtom(builder.newInstance(IAtom.class, ("C")));
        atomContainer.addAtom(builder.newInstance(IAtom.class, ("C")));
        atomContainer.addAtom(builder.newInstance(IAtom.class, ("C")));
        atomContainer.addBond(0, 1, IBond.Order.SINGLE);
        atomContainer.addBond(0, 2, IBond.Order.SINGLE);
        atomContainer.addBond(0, 3, IBond.Order.SINGLE);
        return atomContainer;
    }
    
    @Test
    public void listMultisets() {
        IAtomContainer parent = make3Star();
        AtomSymmetricChildLister lister = new AtomSymmetricChildLister();
        for (int[] intArr : lister.getBondOrderArrays(parent, 3, 4, 3)) {
            System.out.println(Arrays.toString(intArr));
        }
    }
    
    @Test
    public void getAutG() {
        IAtomContainer parent = make3Star();
        AtomSymmetricChildLister lister = new AtomSymmetricChildLister();
        SSPermutationGroup autG = lister.getGroup(parent);
        for (Permutation p : autG.all()) {
            System.out.println(p);
        }
    }
    
    @Test
    public void getMinimalBondOrderArrays() {
        IAtomContainer parent = make3Star();
        int len = parent.getAtomCount();
        AtomSymmetricChildLister lister = new AtomSymmetricChildLister();
        SSPermutationGroup autG = lister.getGroup(parent);
        for (int[] bondOrderArray : lister.getBondOrderArrays(parent, 3, len, 3)) {
            if (lister.isMinimal(bondOrderArray, autG)) {
                System.out.println("YES " + Arrays.toString(bondOrderArray));
            } else {
                System.out.println("NO  " + Arrays.toString(bondOrderArray));
            }
        }
    }
    
    @Test
    public void listChildren() {
        IAtomContainer parent = make3Star();
        AtomContainerPrinter.print(parent);
        AtomSymmetricChildLister lister = new AtomSymmetricChildLister(elementSymbols("CCCCC"));
        int len = parent.getAtomCount();
        for (IAtomContainer child : lister.listChildren(parent, len)) {
            AtomContainerPrinter.print(child);
        }
    }
    
    @Test
    public void nonRedundantChildren() {
        IAtomContainer parent = make3Star();
        AtomContainerPrinter.print(parent);
        AtomSymmetricChildLister lister = new AtomSymmetricChildLister(elementSymbols("CCCCC"));
        int len = parent.getAtomCount();
        List<String> certs = new ArrayList<String>();
        for (IAtomContainer child : lister.listChildren(parent, len)) {
            MoleculeSignature molSig = new MoleculeSignature(child);
            String cert = molSig.toCanonicalString();
            if (certs.contains(cert)) {
                System.out.println("DUP! " + AtomContainerPrinter.toString(child));
            }
        }
    }
}
