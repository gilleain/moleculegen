package test.generate;

import generate.AtomFilteringChildLister;

import io.AtomContainerPrinter;

import java.util.Arrays;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;



public class AtomFilteringChildListerTest {
    
    private static final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    @Test
    public void disconnectedParentTest() {
        String acp = "C0C1C2 0:1(1)";
        IAtomContainer parent = AtomContainerPrinter.fromString(acp, builder);
        parent.getAtom(0).setImplicitHydrogenCount(3);
        parent.getAtom(1).setImplicitHydrogenCount(3);
        parent.getAtom(2).setImplicitHydrogenCount(4);
        AtomFilteringChildLister lister = new AtomFilteringChildLister();
        lister.setElementSymbols(Arrays.asList("C", "C", "C", "O"));
        for (IAtomContainer child : lister.listChildren(parent, 3)) {
            AtomContainerPrinter.print(child);
        }
    }

}
