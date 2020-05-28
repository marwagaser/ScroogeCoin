import java.io.IOException;
import java.security.PublicKey;
import java.security.SignedObject;

public class createCoinTransaction extends transaction {

	public createCoinTransaction(int transID, SignedObject coin) {
		super(transID, coin);
		// TODO Auto-generated constructor stub
	}

	public String stringify() {
		return "<Transaction ID: " + this.transID + ", Previous Transaction Hash: " + this.prevTransHash
				+ ", Signed Coin: " + this.coin + "> ";
	}

	public String print() throws ClassNotFoundException, IOException {
		coin coinx = (coin) this.coin.getObject();
		return "<Transaction ID: " + this.transID + ", Sender : Scrooge" + ", Previous Transaction Hash: "
				+ this.prevTransHash + ", Signed Coin by Scrooge : Coin # " + coinx.id + "> ";
	}
}
