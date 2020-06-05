import java.io.IOException;
import java.security.PublicKey;
import java.security.SignedObject;

public class transferCoinTransaction extends transaction {

	public transferCoinTransaction(int transID, PublicKey senderID, PublicKey receiverID, SignedObject coin) {
		super(transID, coin);
		// TODO Auto-generated constructor stub
		this.senderPU = senderID;
		this.receiverPU = receiverID;
	
	}

	public String stringify() throws ClassNotFoundException, IOException {
		coin coinx = (coin) this.coin.getObject();
		return "<Transaction ID: " + this.transID + ", Previous Transaction Hash: " + this.prevTransHash +", Transaction Hash: " + this.hash + ", Amount: 1"
				+ ", Coin ID: " + coinx.id + ", Sender: " + this.senderPU + ", Receiver: " + this.receiverPU + "> ";
	}

	public String print() throws ClassNotFoundException, IOException {
		coin coinx = (coin) this.coin.getObject();
		return "<Transaction ID: " + this.transID + ", Previous Transaction Hash: " + this.prevTransHash +", Transaction Hash: " + this.hash + "Amount: 1"
				+ ", Coin ID: " + coinx.id + ", Sender: " + this.senderPU + ", Receiver: " + this.receiverPU + "> ";
/*		return "<Transaction ID: " + this.transID + ", Previous Transaction Hash: " + this.prevTransHash +", Transaction Hash: " + this.hash + ", Amount: 1"
		+ ", Coin ID: " + coinx.id +"> ";*/
	}
}
