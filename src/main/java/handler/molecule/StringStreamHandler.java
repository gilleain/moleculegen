package handler.molecule;

import org.openscience.cdk.interfaces.IAtomContainer;

import handler.Handler;
import io.AtomContainerPrinter;

public class StringStreamHandler implements Handler<IAtomContainer> {

    private StringBuilder out;

    private int count;

    private boolean showCount;

    public StringStreamHandler(StringBuilder out) {
        this(out, true);
    }

    public StringStreamHandler(StringBuilder in, boolean showCount) {
        this.showCount = showCount;
        this.count = 0;
        out = in;
    }

    @Override
    public void handle(IAtomContainer atomContainer) {
        if (showCount) {
            out.append(String.valueOf(count));
            out.append("\t");
        }
        if(atomContainer == null)
            return;
        String str = AtomContainerPrinter.toString(atomContainer);
        if(str == null)
            return;
        out.append(str);
        out.append("\n");
        count++;
    }

    @Override
    public void finish() {
    }

}
