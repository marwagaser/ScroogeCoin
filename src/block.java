import java.util.ArrayList;

public class block {
	int blockID;
	ArrayList<transaction> transactions;
	String previousBlockHash;
	String hash;

	public block(int blockID) {
		this.blockID = blockID;
		this.transactions = new ArrayList<transaction>();
	}

	public String stringify() {
		String s = "<Block ID: " + this.blockID + ", Previous Block Hash:" + this.previousBlockHash
				+ ", Transactions: [ ";
		for (int i = 0; i < transactions.size(); i++) {
			s += transactions.get(i).print() + " ";
		}
		s += "] >";
		return s;
	}

	public String print() {
		String s = "<Block ID: " + this.blockID + ", Block Hash:" + this.hash + ", Previous Block Hash:"
				+ this.previousBlockHash + ", Transactions: [ ";
		for (int i = 0; i < transactions.size(); i++) {
			s += transactions.get(i).print() + " ";
		}
		s += "] >";
		return s;
	}
}
