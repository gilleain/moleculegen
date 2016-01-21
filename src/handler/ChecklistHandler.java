package handler;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;

/**
 * Check each generated structure against a list of expected structures -
 * used for testing against results from other software.
 * 
 * @author maclean
 *
 */
public class ChecklistHandler implements Handler {
	
	/**
	 * These are the ones we expect to see
	 */
	private final List<String> expectedList;
	
	/**
	 * These are generated unexpectedly
	 */
	private final List<String> surpriseList;
	
	private final SmilesGenerator smilesGenerator;
	
	private final BitSet checklist;
	
	public ChecklistHandler(String inputFile) throws IOException {
		this.expectedList = new ArrayList<String>();
		// TODO : different input formats?
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		String line;
		while ((line = reader.readLine()) != null) {
			expectedList.add(line);
		}
		reader.close();
		smilesGenerator = SmilesGenerator.unique();
		checklist = new BitSet(expectedList.size());
		this.surpriseList = new ArrayList<String>();
	}

	@Override
	public void handle(IAtomContainer atomContainer) {
		try {
			String observed = smilesGenerator.create(atomContainer);
			int index = expectedList.indexOf(observed);
			if (index == -1) {
				surpriseList.add(observed);
			} else {
				checklist.set(index);
			}
		} catch (CDKException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void finish() {
		for (int index = checklist.nextClearBit(0); index >= 0 && index < expectedList.size(); index = checklist.nextClearBit(index + 1)) {
			System.out.println("Missing " + expectedList.get(index));
		}
		for (String surprise : surpriseList) {
			System.out.println("Surprise " + surprise);
		}
	}

}
