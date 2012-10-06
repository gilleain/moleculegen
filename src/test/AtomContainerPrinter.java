package test;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class AtomContainerPrinter {

    public static void print(IAtomContainer atomContainer) {
        System.out.println(AtomContainerPrinter.toString(atomContainer));
    }

    public static String toString(IAtomContainer atomContainer) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (IAtom atom : atomContainer.atoms()) {
            sb.append(atom.getSymbol()).append(i);
            i++;
        }
        sb.append(" ");
        
        i = 0;
        for (IBond bond : atomContainer.bonds()) {
            int a0 = atomContainer.getAtomNumber(bond.getAtom(0));
            int a1 = atomContainer.getAtomNumber(bond.getAtom(1));
            int o = bond.getOrder().ordinal() + 1;
            sb.append(a0 + ":" + a1 + "(" + o + ")");
            if (i < atomContainer.getBondCount() - 1) {
                sb.append(",");
            }
            i++;
        }
        return sb.toString();
    }
}
