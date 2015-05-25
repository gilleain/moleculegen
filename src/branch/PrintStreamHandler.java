package branch;

import io.AtomContainerPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.openscience.cdk.interfaces.IAtomContainer;

public class PrintStreamHandler implements Handler {
    
    private BufferedWriter out;
    
    private int count;
    
    private boolean showCount;
    
    public PrintStreamHandler(PrintStream out) {
        this(out, true);
    }
        
    public PrintStreamHandler(PrintStream out, boolean showCount) {
        this.out = new BufferedWriter(new PrintWriter(out));
        this.showCount = showCount;
        this.count = 0;
    }

    @Override
    public void handle(IAtomContainer atomContainer) {
        try {
            if (showCount) {
                out.write(String.valueOf(count));
                out.write("\t");
            }
            out.write(AtomContainerPrinter.toString(atomContainer));
            out.newLine();
            out.flush();
            count++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
