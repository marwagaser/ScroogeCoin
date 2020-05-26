import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignedObject;

public class transferCoinTransaction extends transaction {
	PublicKey senderPU;
	PublicKey receiverPU;
	boolean isScrooge;

	public transferCoinTransaction(int transID, PublicKey senderID, PublicKey receiverID, SignedObject coin) {
		super(transID, coin);
		// TODO Auto-generated constructor stub
		this.senderPU = senderID;
		this.receiverPU = receiverID;
		this.isScrooge = false;
	}

	public String stringify() {
		return "<Transaction ID: " + this.transID + ", Sender ID: " + this.senderPU + ", Receiver: " + this.receiverPU
				+ ", Previous Transaction Hash: " + this.prevTransHash + ", Signed Coin: " + this.coin + "> ";
	}

	public String print() throws ClassNotFoundException, IOException {
		coin coinx = (coin) this.coin.getObject();
		if (this.isScrooge) {
			return "<Transaction ID: " + this.transID + ", Sender ID: Scrooge" + ", Receiver: " + this.receiverPU
					+ ", Previous Transaction Hash: " + this.prevTransHash + ", Signed Coin by Scrooge to User "
					+ this.receiverPU + ": Coin # " + coinx.id + "> ";
		} else
			return "<Transaction ID: " + this.transID + ", Sender ID: " + this.senderPU + ", Receiver: "
					+ this.receiverPU + ", Previous Transaction Hash: " + this.prevTransHash + ", Signed Coin by User " + this.senderPU + " to User " + this.receiverPU
					+ ": Coin # " + coinx.id + "> ";
	}
}
