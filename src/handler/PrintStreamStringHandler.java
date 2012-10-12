package handler;

import java.io.PrintStream;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.smiles.SmilesGenerator;

/**
 * Prints the generated molecules in a string form to a print 
 * stream, defaulting to System out. 
 * 
 * @author maclean
 *
 */
public class PrintStreamStringHandler implements GenerateHandler {
	
	private PrintStream printStream;
	
	private SmilesGenerator smilesGenerator;
	
	private int count;
	
	private DataFormat format;
	
	private boolean shouldNumberLines;
	
	public PrintStreamStringHandler() {
		this(System.out, DataFormat.SMILES);
	}
	
	public PrintStreamStringHandler(PrintStream printStream, DataFormat format) {
	    this(printStream, format, true);
	}
	
	public PrintStreamStringHandler(PrintStream printStream, DataFormat format, boolean numberLines) {
		this.printStream = printStream;
		this.format = format;
		smilesGenerator = new SmilesGenerator();
		count = 0;
		this.shouldNumberLines = numberLines;
	}

	@Override
	public void handle(IAtomContainer parent, IAtomContainer child) {
	    String childString = getStringForm(child);
	  
	    boolean debug = false;
//        debug = true;
	    if (debug) {
	        String parentString = getStringForm(parent);
            printStream.println(
                    count + "\t" + parentString
                    + "\t" + test.AtomContainerPrinter.toString(parent)
                    + "\t" + childString 
                    + "\t" + test.AtomContainerPrinter.toString(child));
	    } else {
	        if (shouldNumberLines) { 
                printStream.println(count + "\t" + childString);
            } else {
                printStream.println(childString);
            }
	    }
	    count++;
	}
	
	@Override
    public void finish() {
	    printStream.close();
    }

    private String getStringForm(IAtomContainer atomContainer) {
	    if (format == DataFormat.SMILES) {
	        return smilesGenerator.createSMILES(atomContainer);
	    } else if (format == DataFormat.SIGNATURE) {
	        MoleculeSignature childSignature = new MoleculeSignature(atomContainer);
	        return childSignature.toCanonicalString();
        } else {
            return "";  // XXX
        }
	}
}
