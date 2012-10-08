package handler;

import java.io.PrintStream;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.smiles.SmilesGenerator;

/**
 * Prints the generated molecules to a print stream, defaulting to System out. 
 * 
 * @author maclean
 *
 */
public class PrintStreamHandler implements GenerateHandler {
	
	private PrintStream printStream;
	
	public enum OutputFormat {
	    SMILES,
	    SIGNATURE
	};
	
	private SmilesGenerator smilesGenerator;
	
	private int count;
	
	private OutputFormat format;
	
	public PrintStreamHandler() {
		this(System.out, OutputFormat.SMILES);
	}
	
	public PrintStreamHandler(PrintStream printStream, OutputFormat format) {
		this.printStream = printStream;
		this.format = format;
		smilesGenerator = new SmilesGenerator();
		count = 0;
	}

	@Override
	public void handle(IAtomContainer parent, IAtomContainer child) {
	    if (format == OutputFormat.SMILES) {
	        printAsSmiles(parent, child);
	    } else if (format == OutputFormat.SIGNATURE) {
	        printAsSignature(parent, child);
	    }
	    count++;
	}
	
	private void printAsSmiles(IAtomContainer parent, IAtomContainer child) {
	    String psmiles = smilesGenerator.createSMILES(parent);
        String smiles = smilesGenerator.createSMILES(child);
        boolean debug = false;
        debug = true;
        if (debug) {
            printStream.println(count + "\t" + psmiles 
                    + "\t" + test.AtomContainerPrinter.toString(parent)
                    + "\t" + smiles + "\t" + test.AtomContainerPrinter.toString(child));
        } else {
            printStream.println(count + "\t" + smiles);
        }
	}
	
	private void printAsSignature(IAtomContainer parent, IAtomContainer child) {
	    MoleculeSignature childSignature = new MoleculeSignature(child);
	    printStream.println(count + "\t" + childSignature.toCanonicalString());
	}

}
