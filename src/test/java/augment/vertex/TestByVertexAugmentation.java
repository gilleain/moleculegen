package augment.vertex;

import org.junit.Test;

import augment.constraints.VertexColorConstraints;
import model.Graph;

/**
 * Test augmentation of molecules by single atoms and sets of bonds.
 * 
 * @author maclean
 *
 */
public class TestByVertexAugmentation {
   
    private Graph make(String acpString) {
        return new Graph(acpString);
    }
    
    private ByVertexAugmentation makeAugmentation(Graph mol, String elementSymbol, int... points) {
        VertexColorConstraints e = new VertexColorConstraints(elementSymbol);
        return new ByVertexAugmentation(mol, elementSymbol, points, e);
    }
    
    private boolean test(String start, String atom, int... points) {
        VertexCanonicalChecker checker = new VertexCanonicalChecker();
        Graph mol = make(start);
        ByVertexAugmentation aug = makeAugmentation(mol, atom, points);
        Graph augMol = aug.getAugmentedObject();
        boolean isCanonical = checker.isCanonical(aug);
        System.out.println(isCanonical + "\t" + augMol.toString());
        return isCanonical;
    }
    
    @Test
    public void testDuplicateNines() {
        test("C0C1C2C3C4C5C6C7 0:1(1),0:2(1),0:3(1),1:4(2),4:5(1),5:6(1),6:7(2)", "C", 0, 0, 0, 0, 0, 0, 1, 0);
        test("C0C1C2C3C4C5C6C7 0:1(2),0:2(1),0:3(1),2:4(1),4:5(2),5:6(1),6:7(1)", "C", 0, 0, 0, 0, 0, 0, 1, 0);
    }

}
