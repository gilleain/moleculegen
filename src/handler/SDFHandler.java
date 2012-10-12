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
public class SDFHandler implements GenerateHandler {
	
	private SDFWriter writer;
	
	private int count;
	
	public SDFHandler(String outfile) throws IOException {
	    writer = new SDFWriter(new FileWriter(outfile));
		count = 0;
	}

	@Override
	public void handle(IAtomContainer parent, IAtomContainer child) {
	    try {
            writer.write(child);
        } catch (CDKException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    count++;
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
