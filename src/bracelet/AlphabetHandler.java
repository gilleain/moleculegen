package bracelet;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts between an alphabet of strings and the int[] form of a bracelet.
 * 
 * @author maclean
 *
 */
public class AlphabetHandler implements BraceletHandler {
	
	private String[] alphabet;
	
	private List<List<String>> convertedBracelets;
	
	public AlphabetHandler(String[] alphabet) {
		this.alphabet = alphabet;
		convertedBracelets = new ArrayList<List<String>>();
	}

	@Override
	public void handle(int[] bracelet) {
		List<String> convertedBracelet = new ArrayList<String>();
		for (int x : bracelet) {
			convertedBracelet.add(alphabet[x]);
		}
		convertedBracelets.add(convertedBracelet);
	}

	public List<List<String>> getConvertedBracelets() {
		return convertedBracelets;
	}

}
