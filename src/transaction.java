import java.io.IOException;
import java.io.Serializable;
import java.security.SignedObject;

public class transaction implements Serializable{
	int transID;
	String prevTransHash;
	String hash;
	SignedObject coin;

	public transaction(int transID,SignedObject coin) {
		this.transID = transID;
		this.coin = coin;
	}

}
