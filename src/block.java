import java.io.IOException;
import java.security.SignedObject;
import java.util.ArrayList;

public class block {
	int blockID; //block ID
	ArrayList<SignedObject> transactions; //signed array list of transactions
	String previousBlockHash; //the hash of the previous block
	String hash; //the hash of the block

	public block(int blockID) {
		this.blockID = blockID;
		this.transactions = new ArrayList<SignedObject>();
	}

/*	public String stringify() {
		String s = "<Block ID: " + this.blockID + ", Previous Block Hash:" + this.previousBlockHash
				+ ", Transactions: [ ";
		for (int i = 0; i < transactions.size(); i++) {
			s += transactions.get(i) + " ";
		}
		s += "] >";
		return s;
	}*/

	public String stringify() throws ClassNotFoundException, IOException {
		String s = "<Block ID: " + this.blockID + ", Previous Block Hash:" + this.previousBlockHash + ", Block Hash:"
				+ this.hash + ", Transactions: [ ";
		for (int i = 0; i < transactions.size(); i++) {

			if ((transactions.get(i).getObject()) instanceof createCoinTransaction) {
				createCoinTransaction curr = (createCoinTransaction) (transactions.get(i).getObject());
				s += curr.stringify() + " ";
			} else {

				transferCoinTransaction curr = (transferCoinTransaction) (transactions.get(i).getObject());
				s += curr.stringify() + " ";
			}
		}
		s += "] >";
		return s;
	}
	

	public String blockchain_member_print() throws ClassNotFoundException, IOException {
		String s = "<Block ID: " + this.blockID + ", Previous Block Hash:" + this.previousBlockHash + ", Block Hash:"
				+ this.hash +">";
		return s;
	}
	
	public String printunderConstruction() throws ClassNotFoundException, IOException {
		String s = "<Block Under Construction ID: " + this.blockID + ", Transactions: [ ";
		for (int i = 0; i < transactions.size(); i++) {

			if ((transactions.get(i).getObject()) instanceof createCoinTransaction) {
				createCoinTransaction curr = (createCoinTransaction) (transactions.get(i).getObject());
				s += curr.print() + " ";
			} else {

				transferCoinTransaction curr = (transferCoinTransaction) (transactions.get(i).getObject());
				s += curr.print() + " ";
			}
		}
		s += "]>" +"\n";
		return s;
	}
}
