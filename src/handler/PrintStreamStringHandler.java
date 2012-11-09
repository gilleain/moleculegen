package handler;

import io.AtomContainerPrinter;

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
	
	private boolean showParent;
	
	public PrintStreamStringHandler() {
		this(System.out, DataFormat.SMILES);
	}
	
	public PrintStreamStringHandler(PrintStream printStream, DataFormat format) {
	    this(printStream, format, false, false);
	}
	
	public PrintStreamStringHandler(PrintStream printStream, 
	                                DataFormat format, 
	                                boolean numberLines,
	                                boolean showParent) {
		this.printStream = printStream;
		this.format = format;
		if (format == DataFormat.SMILES) {
		    smilesGenerator = new SmilesGenerator();
		}
		count = 0;
		this.shouldNumberLines = numberLines;
		this.showParent = showParent;
	}

	@Override
	public void handle(IAtomContainer parent, IAtomContainer child) {
	    String childString = getStringForm(child);
	  
	    if (showParent) {
	        String parentString = getStringForm(parent);
            printStream.println(count + "\t" + parentString + "\t" + childString); 
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
	    } else if (format == DataFormat.ACP) {
	        return AtomContainerPrinter.toString(atomContainer);
        } else {
            return "";  // XXX
        }
	}
}
