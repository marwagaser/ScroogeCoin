import java.io.IOException;
import java.security.PublicKey;
import java.security.SignedObject;

public class createCoinTransaction extends transaction {

	public createCoinTransaction(int transID, SignedObject coin,PublicKey receiverPU) {
		super(transID, coin);
		this.senderPU = receiverPU; //The sender and the receiver are both scrooge since he created the coin, so it is his
		this.receiverPU =receiverPU;
		// TODO Auto-generated constructor stub
	}

	public String stringify() throws ClassNotFoundException, IOException {
		coin coinx = (coin) this.coin.getObject();
		return "<Transaction ID: " + this.transID + ", Previous Transaction Hash: " + this.prevTransHash
				+ ", Signed Coin: " + coinx.id + "> ";
	}

	public String print() throws ClassNotFoundException, IOException {
		coin coinx = (coin) this.coin.getObject();
		return "<Coin Creation Transaction ID: " + this.transID + ", Sender : Scrooge" + ", Previous Transaction Hash: "
				+ this.prevTransHash  +", Transaction Hash: " + this.hash + ", Signed Coin by Scrooge : Coin # " + coinx.id + ">" +"\n";
	}
}
