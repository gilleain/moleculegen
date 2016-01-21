package test.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import canonical.NautyLikeCanonicalChecker;
import io.AtomContainerPrinter;

public class TestNautyLikeCanonicalChecker {
    
    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    @Test
    public void testA() {
        IAtomContainer ac = AtomContainerPrinter.fromString("C0C1C2 0:1(1),1:2(2)", builder);
        IAtomContainer transformed = NautyLikeCanonicalChecker.transform(ac);
        assertEquals("Should have 6 vertices", 6, transformed.getAtomCount());
        AtomContainerPrinter.print(transformed);
        
        // 'backbone' edges
        assertEdge(transformed, 0, 1);
        assertEdge(transformed, 2, 3);
        assertEdge(transformed, 4, 5);
        
        // bond order edges
        assertEdge(transformed, 0, 2);  // single bond
        assertEdge(transformed, 3, 5);  // double bond
    }
    
    @Test
    public void testB() {
        IAtomContainer ac = AtomContainerPrinter.fromString("C0C1C2C3 0:1(1),0:3(2),1:2(2),2:3(1)", builder);
        IAtomContainer transformed = NautyLikeCanonicalChecker.transform(ac);
        assertEquals("Should have 8 vertices", 8, transformed.getAtomCount());
        
        assertEdge(transformed, 0, 2);
        assertEdge(transformed, 1, 7);
        assertEdge(transformed, 3, 5);
        assertEdge(transformed, 4, 6);
    }
    
    @Test
    public void testC() {
        IAtomContainer ac = AtomContainerPrinter.fromString("C0C1C2C3 0:1(2),0:2(1),1:2(1),2:3(3)", builder);
        IAtomContainer transformed = NautyLikeCanonicalChecker.transform(ac);
        assertEquals("Should have 12 vertices", 12, transformed.getAtomCount());
        
        assertEdge(transformed, 0, 6);
        assertEdge(transformed, 1, 4);
        assertEdge(transformed, 3, 6);
        assertEdge(transformed, 8, 11);
    }
    
    @Test
    public void testBPair() {
        IAtomContainer ac1 = AtomContainerPrinter.fromString("C0C1C2C3 0:1(1),0:3(2),1:2(2),2:3(1)", builder);
        IAtomContainer transformed1 = NautyLikeCanonicalChecker.transform(ac1);
        boolean b1 = NautyLikeCanonicalChecker.isCanonical(ac1, set(0, 2));
        
        IAtomContainer ac2 = AtomContainerPrinter.fromString("C0C1C2C3 0:1(1),0:2(2),1:3(2),2:3(1)", builder);
        IAtomContainer transformed2 = NautyLikeCanonicalChecker.transform(ac2);
        boolean b2 = NautyLikeCanonicalChecker.isCanonical(ac2, set(1, 2));
        System.out.println(b1 + " " + b2);
    }
    
    @Test
    public void testFailingTenA() {
        IAtomContainer ac = AtomContainerPrinter.fromString(
                "C0C1C2C3 0:1(1),0:3(2),1:2(2),2:3(1)", builder);
    }
    
    private List<Integer> set(int... ints) {
        List<Integer> set = new ArrayList<Integer>();
        for (int i : ints){ 
            set.add(i);
        }
        return set;
    }
    
    private void assertEdge(IAtomContainer ac, int a0i, int a1i) {
        boolean found = false;
        for (IBond bond : ac.bonds()) {
            IAtom a0 = bond.getAtom(0);
            IAtom a1 = bond.getAtom(1);
            int a0j = ac.getAtomNumber(a0);
            int a1j = ac.getAtomNumber(a1);
            found = (a0i == a0j && a1i == a1j) || (a1i == a0j && a0i == a1j);
            if (found) break;
        }
        assertTrue(String.format("No edge (%s, %s)", a0i, a1i), found);
    }

}
