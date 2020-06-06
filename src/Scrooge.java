import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignedObject;
import java.util.ArrayList;
import java.util.HashMap;

public class Scrooge {
 SignedObject lastHashPointer; //An object which will hold the signature of the hash of the latest block
int id; //Scrooge ID 
HashMap <Integer, SignedObject> signedcoins; //An array list which contains the coin IDs and the transactions they currently exist in
PublicKey publicKey;//Scrooge PUK
private PrivateKey privateKey; //Scrooge PRK (Notice that the scope is private)
HashMap <PublicKey ,  ArrayList<SignedObject>> users_coins; //contains the PUK and the list of coins for each user

public Scrooge (KeyPair kp) {
	this.id = 100;      
	this.signedcoins= new HashMap <Integer, SignedObject>();
	this.users_coins= new HashMap <PublicKey ,  ArrayList<SignedObject>> ();
	this.setPrivateKey(kp.getPrivate());
	this.publicKey = kp.getPublic();
}
public boolean verifySignature(SignedObject message, PublicKey PUK) throws Exception { //verifies the signature of a certained signed object
	Signature PUsignature = Signature.getInstance("SHA1withRSA"); // create signature object
	boolean isVerified = message.verify(PUK, PUsignature);
	return isVerified;
}

public coin createaCoin(int coinID) {//A method to create coins
	return new coin(coinID);
}
public boolean isDoubleSpending(block b, SignedObject signedtrans) throws ClassNotFoundException, IOException { // A method used to check for double spending
	transferCoinTransaction currentTrans = (transferCoinTransaction) signedtrans.getObject(); //get the transaction we want to check for the double spending in, along with the accumulator it should be added to
	int currentID = ((coin)currentTrans.coin.getObject()).id; //get the ID of the coin in the the transaction
	PublicKey currentPublicKey = currentTrans.senderPU; //get the PUK of the sender of that coin in the transaction
	for (int i=0; i<b.transactions.size();i++) { //for every transaction in the accumulator
			transferCoinTransaction tct = (transferCoinTransaction) b.transactions.get(i).getObject();
			PublicKey senderPUK = tct.senderPU; //check the PUK of the sender
			int cid = ((coin)tct.coin.getObject()).id; //and the ID of the coin
			if (cid == currentID && currentPublicKey.equals(senderPUK)) { //if the id of the coin and the sender are identical
				return true; //then it's a double spending attack.
			}
	}
	return false;
}
public PrivateKey getPrivateKey() {
	return privateKey;
}
public void setPrivateKey(PrivateKey privateKey) {
	this.privateKey = privateKey;
}
}
