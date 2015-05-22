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
    
    public PrintStreamHandler(PrintStream out) {
        this.out = new BufferedWriter(new PrintWriter(out));
        this.count = 0;
    }

    @Override
    public void handle(IAtomContainer atomContainer) {
        try {
            out.write(String.valueOf(count));
            out.write("\t");
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
