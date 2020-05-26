import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignedObject;
import java.util.ArrayList;

public class user {
	int id;
	private PrivateKey PRkey;
	 PublicKey PUKey;

	public user(int id, PrivateKey PRKey, PublicKey PUKey) {
		this.id = id;	
		this.setPRkey(PRKey);
		this.PUKey = PUKey;
	}

	public String print() {
		return "<User ID: " + this.id + ", Public Key: " + this.PUKey + ">";
	}

	public PrivateKey getPRkey() {
		return PRkey;
	}

	public void setPRkey(PrivateKey pRkey) {
		PRkey = pRkey;
	}

}
