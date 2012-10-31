package bracelet;

import java.util.Arrays;

/**
 * Generate bracelets (circular sequences) based on Joe Sawada's algorithm in
 * 
 * "Generating Bracelets in Constant Amortized Time" Siam Journal of Computing, 
 *  Vol. 31, No. 1, pp. 259Ð268.
 * 
 * @author maclean
 *
 */
public class BraceletGenerator {
	
	/**
	 * The storage space for the bracelet(s) being generated.
	 */
	private int[] bracelet;
	
	/**
	 * Handler to print or deal with the bracelets as they are generated. 
	 */
	private BraceletHandler handler;

	// XXX min/max necessary as the algorithm goes from 1:N not 0:N-1
	private int max; 		// the max index
	private int min;	// the min index

	/**
	 * The number of colors.
	 */
	private int colors;

	/**
	 * Specific counts of colors.
	 */
	private int[] colorCounts;

	public BraceletGenerator(int n, int k) {
		this(n, k, new PrintHandler());
	}

	public BraceletGenerator(int n, int k, BraceletHandler handler) {
		this.colors = k;
		max = n;
		this.bracelet = new int[n + 1];
		min = 1;
		this.handler = handler;
	}

	public BraceletGenerator(int[] colorCounts) {
		this(colorCounts, new PrintHandler());
	}

	public BraceletGenerator(int[] colorCounts, BraceletHandler handler) {
		int n = 0;
		for (int count : colorCounts) {
			n += count;
		}
		min = 1;
		max = n;
		colors = colorCounts.length;
		this.bracelet = new int[n + 1];
		this.colorCounts = colorCounts;
		this.handler = handler;
	}

	public void genBracelets() {
		genBracelets(1, 1, 0, 0, 0, false);
	}

	public void genBracelets(int t, int p, int r, int u, int v, boolean RS) {

		if (t - 1 > ((max - r) / 2) + r) {
			int idx = max - t + 2 + r;
			if (bracelet[t - 1] > bracelet[idx]) RS = false;
			else if (bracelet[t - 1] < bracelet[idx]) RS = true;
		}

		if (t > max) {
			if (!RS && (max % p == 0)) printIt(); 
		} else {
			bracelet[t] = bracelet[t - p];

			if (bracelet[t] == bracelet[min]) {
				v = v + 1;
			} else {
				v = 0;
			}

			if (u == t - 1 && bracelet[t - 1] == bracelet[min]) {
				u = u + 1;
			} 

			if (t == max && u != max && bracelet[max] == bracelet[min]) {
				// skip
			} else if (u == v) {
				int rev = checkRev(t, u);
				if (rev == 0) genBracelets(t + 1, p, r, u, v, RS);
				if (rev == 1) genBracelets(t + 1, p, t, u, v, false);
			} else {
				genBracelets(t + 1, p, r, u, v, RS);
			}
			if (u == t) u = u - 1;

			for (int j = bracelet[t - p] + 1; j < colors; j++) {
				bracelet[t] = j;
				if (t == min) {
					genBracelets(t + 1, t, r, 1, 1, RS);
				} else {
					genBracelets(t + 1, t, r, u, 0, RS);
				}
			}
		}
	}

	private int checkRev(int t, int i) {
		int max = (t + 1) / 2;
		for (int j = i + 1; j <= max; j++) {
			int x = t - j + 1;
			if (bracelet[j] < bracelet[x]) return 0;
			if (bracelet[j] > bracelet[x]) return -1;
		}
		return 1;
	}

	public void printIt() {
		// check the actual color counts if necessary
		if (colorCounts != null) {
			int[] actualCounts = new int[colors]; 
			for (int i = min; i <= max; i++) {
				int x = bracelet[i];
				actualCounts[x]++;
				if (actualCounts[x] > colorCounts[x]) return;
			}
			if (!Arrays.equals(colorCounts, actualCounts)) return;
		}
		
		// this is essential for to reasons :
		// 1) To remove the first 0 (see min/max comment)
		// 2) Must not pass a reference to the array, as it will be altered
		int[] subCopy = Arrays.copyOfRange(bracelet, min, max + 1);
		handler.handle(subCopy);
	}

}
