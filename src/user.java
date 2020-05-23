import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignedObject;
import java.util.ArrayList;

public class user {
	int id;
	ArrayList<SignedObject> coins;
	PrivateKey PRkey;
	PublicKey PUKey;
	int tempcoins;

	public user(int id, PrivateKey PRKey, PublicKey PUKey) {
		this.id = id;
		this.coins = new ArrayList<SignedObject>();
		this.PRkey = PRKey;
		this.PUKey = PUKey;
		this.tempcoins = 10;
	}

	public String stringify() {
		return "<User ID: " + this.id + ", Coins: " + this.coins.size() + ">";
	}

}
