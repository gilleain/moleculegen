package bracelet;

public class CountingHandler implements BraceletHandler {
	
	private int count;
	
	public int getCount() {
		return count;
	}

	@Override
	public void handle(int[] bracelet) {
		count++;
	}

}
