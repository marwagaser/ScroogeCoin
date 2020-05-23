import java.io.IOException;
import java.security.SignedObject;
import java.util.ArrayList;

public class block {
	int blockID;
	ArrayList<SignedObject> transactions;
	String previousBlockHash;
	String hash;

	public block(int blockID) {
		this.blockID = blockID;
		this.transactions = new ArrayList<SignedObject>();
	}

	public String stringify() {
		String s = "<Block ID: " + this.blockID + ", Previous Block Hash:" + this.previousBlockHash
				+ ", Transactions: [ ";
		for (int i = 0; i < transactions.size(); i++) {
			s += transactions.get(i)+ " ";
		}
		s += "] >";
		return s;
	}

	public String print() throws ClassNotFoundException, IOException {
		String s = "<Block ID: " + this.blockID + ", Block Hash:" + this.hash + ", Previous Block Hash:"
				+ this.previousBlockHash + ", Transactions: [ ";
		for (int i = 0; i < transactions.size(); i++) {
			transaction curr = (transaction) (transactions.get(i).getObject());
			s += curr.print() + " ";
		}
		s += "] >";
		return s;
	}
}
