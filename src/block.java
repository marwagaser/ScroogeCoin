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
			s += transactions.get(i) + " ";
		}
		s += "] >";
		return s;
	}

	public String print2() throws ClassNotFoundException, IOException {
		String s = "<Block ID: " + this.blockID + ", Previous Block Hash:" + this.previousBlockHash + ", Block Hash:"
				+ this.hash + ", Transactions: [ ";
		for (int i = 0; i < transactions.size(); i++) {

			if ((transactions.get(i).getObject()) instanceof createCoinTransaction) {
				System.out.println("cc trans");
				createCoinTransaction curr = (createCoinTransaction) (transactions.get(i).getObject());
				s += curr.print() + " ";
			} else {

				transferCoinTransaction curr = (transferCoinTransaction) (transactions.get(i).getObject());
				s += curr.print() + " ";
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
				System.out.println("cc trans");
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
