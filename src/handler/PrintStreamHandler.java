package handler;

import java.io.PrintStream;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;

/**
 * Prints the generated molecules to a print stream, defaulting to System out. 
 * 
 * @author maclean
 *
 */
public class PrintStreamHandler implements GenerateHandler {
	
	private PrintStream printStream;
	
	private SmilesGenerator smilesGenerator;
	
	private int count;
	
	public PrintStreamHandler() {
		this(System.out);
	}
	
	public PrintStreamHandler(PrintStream printStream) {
		this.printStream = printStream;
		smilesGenerator = new SmilesGenerator();
		count = 0;
	}

	@Override
	public void handle(IAtomContainer parent, IAtomContainer child) {
	    String psmiles = smilesGenerator.createSMILES(parent);
		String smiles = smilesGenerator.createSMILES(child);
//		printStream.println(count + "\t" + psmiles 
//		        + "\t" + test.AtomContainerPrinter.toString(parent)
//		        + "\t" + smiles + "\t" + test.AtomContainerPrinter.toString(child));
//		
		printStream.println(count + "\t" + smiles);
		count++;
	}

}
