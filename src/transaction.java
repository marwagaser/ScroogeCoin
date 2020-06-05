import java.io.IOException;
import java.io.Serializable;
import java.security.PublicKey;
import java.security.SignedObject;

public class transaction implements Serializable{
	int transID;
	String prevTransHash;
	String hash;
	SignedObject coin;
	PublicKey receiverPU;
	PublicKey senderPU;

	public transaction(int transID,SignedObject coin) {
		this.transID = transID;
		this.coin = coin;
		this.receiverPU = null;
		this.senderPU = null;
	}

}
