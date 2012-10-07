package test.generate;

import generate.AtomSymmetricChildLister;
import group.Permutation;
import group.SSPermutationGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.signature.MoleculeSignature;

import test.AtomContainerPrinter;

public class AtomSymmetricChildListerTest {
    
    public IAtomContainer make3Star() {
        IAtomContainer atomContainer = new AtomContainer();
        atomContainer.addAtom(new Atom("C"));
        atomContainer.addAtom(new Atom("C"));
        atomContainer.addAtom(new Atom("C"));
        atomContainer.addAtom(new Atom("C"));
        atomContainer.addBond(0, 1, IBond.Order.SINGLE);
        atomContainer.addBond(0, 2, IBond.Order.SINGLE);
        atomContainer.addBond(0, 3, IBond.Order.SINGLE);
        return atomContainer;
    }
    
    @Test
    public void listMultisets() {
        IAtomContainer parent = make3Star();
        AtomSymmetricChildLister lister = new AtomSymmetricChildLister();
        for (List<Integer> multiset : lister.getMultisets(parent, 4)) {
            int[] intArr = lister.toIntArray(multiset, 4);
            System.out.println(Arrays.toString(intArr) + "\t" + multiset);
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
        for (List<Integer> multiset : lister.getMultisets(parent, len)) {
            int[] bondOrderArray = lister.toIntArray(multiset, len);
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
        parent.addAtom(new Atom("C"));
        AtomContainerPrinter.print(parent);
        AtomSymmetricChildLister lister = new AtomSymmetricChildLister();
        int len = parent.getAtomCount() - 1;
        for (IAtomContainer child : lister.listChildren(parent, len)) {
            AtomContainerPrinter.print(child);
        }
    }
    
    @Test
    public void nonRedundantChildren() {
        IAtomContainer parent = make3Star();
        parent.addAtom(new Atom("C"));
        AtomContainerPrinter.print(parent);
        AtomSymmetricChildLister lister = new AtomSymmetricChildLister();
        int len = parent.getAtomCount() - 1;
        List<String> certs = new ArrayList<String>();
        for (IAtomContainer child : lister.listChildren(parent, len)) {
            MoleculeSignature molSig = new MoleculeSignature(child);
            String cert = molSig.toCanonicalString();
            if (certs.contains(cert)) {
                System.out.println("dup " + AtomContainerPrinter.toString(child));
            }
        }
    }
}
