package handler;

import java.io.FileWriter;
import java.io.IOException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;

/**
 * Prints the generated molecules to a print stream, defaulting to System out. 
 * 
 * @author maclean
 *
 */
public class SDFHandler implements Handler {
	
	private SDFWriter writer;
	
	public SDFHandler() {
        writer = new SDFWriter(System.out);
    }
	
	public SDFHandler(String outfile) throws IOException {
	    writer = new SDFWriter(new FileWriter(outfile));
	}

	@Override
	public void handle(IAtomContainer atomContainer) {
	    try {
            writer.write(atomContainer);
        } catch (CDKException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	@Override
    public void finish() {
	    try {
	        writer.close();
	    } catch (IOException ioe) {
	        // TODO
	    }
    }
}
