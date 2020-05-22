import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class user {
	int id;
	ArrayList<String> coins;
	PrivateKey PRkey;
	PublicKey PUKey;

	public user(int id, PrivateKey PRKey, PublicKey PUKey) {
		this.id = id;
		this.coins = new ArrayList<String>();
		this.PRkey = PRKey;
		this.PUKey = PUKey;
	}

	public String stringify() {
		return "<User ID: " + this.id + ", Coins: " + this.coins.size() + ">";
	}

}
