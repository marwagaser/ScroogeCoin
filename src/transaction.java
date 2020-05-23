import java.io.Serializable;

public class transaction implements Serializable{
	int transID;
	int receiverID;
	int senderID;
	String prevTransHash;
	String hash;
	int amount;

	public transaction(int transID, int receiverID, int senderID, int amount) {
		this.transID = transID;
		this.receiverID = receiverID;
		this.senderID = senderID;
		this.amount = amount;
	}

	public String stringify() {
		return "<Transaction ID: " + this.transID + ", Receiver: " + this.receiverID + ", Sender ID: " + this.senderID
				+ ", Previous Transaction Hash: " + this.prevTransHash + ", Amount: " + this.amount + ">";
	}
	public String print() {
		return "<Transaction ID: " + this.transID + ", Receiver: " + this.receiverID + ", Sender ID: " + this.senderID
				+ ", Previous Transaction Hash: " + this.prevTransHash + ", Transaction Hash: " + this.hash + ", Amount: " + this.amount + ">";
	}
}
