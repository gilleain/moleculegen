package bracelet;

import java.util.Arrays;

public class PrintHandler implements BraceletHandler {
	
	/**
	 * The number of bracelets generated.
	 */
	private int count;
	
	private boolean shouldPrintCount;
	
	public PrintHandler() {
		this(true);
	}
	
	public PrintHandler(boolean shouldPrintCount) {
		this.shouldPrintCount = shouldPrintCount;
	}

	@Override
	public void handle(int[] bracelet) {
		if (shouldPrintCount) {
			System.out.println(count + "\t" + Arrays.toString(bracelet));
			count++;
		} else {
			System.out.println(Arrays.toString(bracelet));
		}
	}

}
