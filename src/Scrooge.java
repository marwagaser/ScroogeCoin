import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignedObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Scrooge {
int id;
HashMap <Integer, SignedObject> signedcoins; //contains where each coin is currently
PublicKey publicKey;
private PrivateKey privateKey;
HashMap <PublicKey ,  ArrayList<SignedObject>> users_coins; //contains the PU and the list of coins for each user

public Scrooge (KeyPair kp) {
	this.id = 100;      
	this.signedcoins= new HashMap <Integer, SignedObject>();
	this.users_coins= new HashMap <PublicKey ,  ArrayList<SignedObject>> ();
	this.setPrivateKey(kp.getPrivate());
	this.publicKey = kp.getPublic();
}
public boolean verifySignature(SignedObject message, PublicKey PUK) throws Exception {
	Signature PUsignature = Signature.getInstance("SHA1withRSA"); // create signature object
	boolean isVerified = message.verify(PUK, PUsignature);
	return isVerified;
}

public boolean isDoubleSpending(block b, SignedObject signedtrans) throws ClassNotFoundException, IOException {
	transferCoinTransaction currentTrans = (transferCoinTransaction) signedtrans.getObject();
	int currentID = ((coin)currentTrans.coin.getObject()).id;
	PublicKey currentPublicKey = currentTrans.senderPU;
	for (int i=0; i<b.transactions.size();i++) {
	
			transferCoinTransaction tct = (transferCoinTransaction) b.transactions.get(i).getObject();
			PublicKey senderPUK = tct.senderPU;
			int cid = ((coin)tct.coin.getObject()).id;
			if (cid == currentID && currentPublicKey.equals(senderPUK)) {
				return true;
			}
	}
	return false;
}
public String print() {
	String s = "";
	 for (Entry<PublicKey, ArrayList<SignedObject>> hmapElement : users_coins.entrySet()) { 
         //PublicKey key = (PublicKey) hmapElement.getKey(); 
         int value = ((int)hmapElement.getValue().size()); 
       s+= "<Public Key: "+hmapElement.getKey()+ ", Coins Owned: "+value+">" +"\n";
     }
	 
	 return s;
}
public PrivateKey getPrivateKey() {
	return privateKey;
}
public void setPrivateKey(PrivateKey privateKey) {
	this.privateKey = privateKey;
}
}
