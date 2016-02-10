package view;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import augment.atom.AtomGenerator;
import handler.molecule.CountingHandler;
import view.tree.PrintingTreeWalker;
import view.tree.TreeBuilder;

public class TestTreeBuilder {
    
    private IAtomContainer mol(String molString) {
        return io.AtomContainerPrinter.fromString(
                molString, SilentChemObjectBuilder.getInstance());
    }
    
    @Test
    public void testBuild() {
        TreeBuilder builder = new TreeBuilder();
        TreeBuilder.TreeCanonicalHandler handler = builder.getHandler();
        IAtomContainer root = mol("C0C1 0:1(1)");
        IAtomContainer m1 = mol("C0C1C2 0:1(1),0:2(1)");
        IAtomContainer m11 = mol("C0C1C2C3 0:1(1),0:2(1),0:3(1)");
        IAtomContainer m2 = mol("C0C1C2 0:1(1),1:2(1)");
        IAtomContainer m21 = mol("C0C1C2C3 0:1(1),1:2(1),2:3(1)");
        handler.handle(root, m1, true);
        handler.handle(m1, m11, true);
        handler.handle(root, m2, true);
        handler.handle(m2, m21, false);
        builder.walkTree(new PrintingTreeWalker());
    }
    
    @Test
    public void buildFromGenerator() {
        TreeBuilder builder = new TreeBuilder(true);
        TreeBuilder.TreeCanonicalHandler handler = builder.getHandler();
        AtomGenerator generator = new AtomGenerator("C4H10", new CountingHandler(false));
        generator.setCanonicalHandler(handler);
        generator.run();
        builder.walkTree(new PrintingTreeWalker());
    }

}
