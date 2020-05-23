import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignedObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;

public class blockchain {
	static String lastHashPointer;
	static SignedObject signedByScroogeHash;
	static int blockID = 0;
	static int transID = 0;
	static block tempBlock = new block(-1);
	static ArrayList<user> users = new ArrayList<user>();
	static ArrayList<SignedObject> transactions = new ArrayList<SignedObject>();
	static LinkedList<block> blocks = new LinkedList<block>();

	public static String displayBlockChain() throws ClassNotFoundException, IOException {
		String s = "";
		for (int i = 0; i < blocks.size(); i++) {
			s += blocks.get(i).print() + "\n";
		}
		return s;
	}

	public static KeyPair getKeyPair() throws Exception {
		KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("DSA"); // use RSA to generate PU-PR key pair
		keypairgenerator.initialize(1024, new SecureRandom());
		KeyPair private_public_key_pair = keypairgenerator.generateKeyPair();
		return private_public_key_pair;
	}

	public static SignedObject sign(Object message, PrivateKey PK) throws Exception {
		Signature PRsignature = Signature.getInstance("DSA"); // create signature object
		SignedObject signedObject = new SignedObject((Serializable) message, PK, PRsignature);
		return signedObject;
	}

	public static boolean verifySignature(SignedObject message, PublicKey PUK) throws Exception {
		Signature PUsignature = Signature.getInstance("DSA"); // create signature object
		boolean isVerified = message.verify(PUK, PUsignature);
		return isVerified;
	}

	public static void transferCoins(ArrayList<SignedObject> transactions) throws Exception {
		System.out.println("tranfering...");
		System.out.println(transactions.size());
		for (int k = 0; k < transactions.size(); k++) {
			transaction unsignedObject = ((transaction) transactions.get(k).getObject());
			int senderID = unsignedObject.senderID;
			int receiverID = unsignedObject.receiverID;
			for (int i = 0; i < unsignedObject.amount; i++) {
				// check that coin
				SignedObject so = users.get(senderID).coins.remove(0);
				users.get(receiverID).coins.add(so);

			}
		}

	}

	public static void notifyScrooge(PublicKey PUK, SignedObject signedTransaction, KeyPair scroogeKeyPair)
			throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		boolean isVerified = verifySignature(signedTransaction, PUK);
		if (isVerified) { // if indeed the sender is the one who sent the message
			transaction unsignedTransaction = ((transaction) signedTransaction.getObject());
			if (users.get(unsignedTransaction.senderID).tempcoins >= unsignedTransaction.amount) { // if they have
																									// enough money
																									// then
																									// transaction
																									// is valid: 1.
																									// Increment
																									// transaction
																									// ID
				users.get(unsignedTransaction.senderID).tempcoins -= unsignedTransaction.amount;
				users.get(unsignedTransaction.receiverID).tempcoins += unsignedTransaction.amount;
				transID += 1;
				System.out.println("generating...");
				System.out.println(tempBlock.transactions.size());
				// publish transaction in tempblock
				if (blocks.isEmpty() && tempBlock.blockID == -1) { // if there aren't blocks yet
					// create one
					tempBlock = new block(blockID);
					tempBlock.previousBlockHash = null;
					tempBlock.transactions.add(signedTransaction);
				} else { // if block still has places
					tempBlock.transactions.add(signedTransaction);

					if (tempBlock.transactions.size() == 10) { // if the block has 10
						// transactions, create a new
						// one and put the old one in the blockchain
						blockID++;
						blocks.add(tempBlock);

						byte[] hash_block = digest
								.digest(blocks.get(blocks.size() - 1).stringify().getBytes(StandardCharsets.UTF_8)); // hash
						// the
						// block
						// Convert byte[] to String
						String hashedBlock = Base64.getEncoder().encodeToString(hash_block);
						blocks.get(blocks.size() - 1).hash = hashedBlock;
						lastHashPointer = hashedBlock;
						signedByScroogeHash = sign(hashedBlock, scroogeKeyPair.getPrivate());
						transferCoins(tempBlock.transactions); // transfer the money
						tempBlock = new block(blockID);
						tempBlock.previousBlockHash = blocks.get(blocks.size() - 1).hash;
					}

				}

				// do actual deduction and additions to the coins arraylist

			} else {
				System.out.println(users.get(unsignedTransaction.senderID).tempcoins);
				System.out.println("no enough money");
			}
		} else {
			System.out.println("Not Verified");
		}
	}

	public static void sendCoin(int senderID, int receiverID, int amount, KeyPair scroogeKeyPair) throws Exception {
	
		user sender = users.get((senderID));
		transaction t = new transaction(transID, receiverID, senderID, amount);
		if (transactions.isEmpty()) {
			t.prevTransHash = null;
		} else {
			transaction prev = (transaction) transactions.get(transactions.size() - 1).getObject();
			t.prevTransHash = prev.hash;
			transactions.clear();
		}
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash_signed_transaction = digest.digest(t.stringify().getBytes(StandardCharsets.UTF_8));
		// Convert byte[] to String
		String hashedTransaction = Base64.getEncoder().encodeToString(hash_signed_transaction);
		t.hash = hashedTransaction;
		// sign the transaction using sender's private key
		SignedObject signedTrans = sign(t, sender.PRkey);
		transactions.add(signedTrans);
		// verify that the transaction came out of the sender
		notifyScrooge(users.get(senderID).PUKey, signedTrans, scroogeKeyPair);

	}

	public static void createCoins(KeyPair scroogePair) throws Exception {
		users.clear();
		blockID = 0;
		transID = 0;
		blocks.clear();
		transactions.clear();
		int coinID = 1;
		for (int i = 0; i < 100; i++) {
			int userId = i;
			KeyPair userPair = getKeyPair();
			user userA = new user(userId, userPair.getPrivate(), userPair.getPublic());
			for (int c = 0; c < 10; c++) {
				coin theCoin = new coin(coinID);
				coinID += 1;
				SignedObject Signedcoin = sign(theCoin, scroogePair.getPrivate()); // sign the coin
				userA.coins.add(Signedcoin);// add the signed coin to the user's arraylist of coins
			}
			users.add(userA);
		}

	}

	public static void main(String[] args) throws Exception {
		KeyPair scroogePair = getKeyPair();
		createCoins(scroogePair);
		System.out.println(users.size());
		int num = 0;
		sendCoin(0, 1, 5, scroogePair);
		sendCoin(0, 1, 5, scroogePair);
		// sendCoin(0, 1, 5, scroogePair);
		sendCoin(1, 2, 5, scroogePair);
		sendCoin(2, 3, 5, scroogePair);
		sendCoin(3, 4, 5, scroogePair);
		sendCoin(4, 5, 5, scroogePair);
		sendCoin(5, 6, 5, scroogePair);
		sendCoin(6, 7, 5, scroogePair);
		sendCoin(7, 8, 5, scroogePair);
		sendCoin(8, 9, 5, scroogePair);
		// sendCoin(9, 10, 5, scroogePair);
		// sendCoin(10, 11, 5, scroogePair);
		// sendCoin(11, 12, 5, scroogePair);
		// sendCoin(0, 1, 5, scroogePair);
		System.out.println(users.get(0).coins.size());
		System.out.println(users.get(1).coins.size());
		/*
		 * while (num < 100) { int user1 = (int) (Math.random() * (users.size())); int
		 * user2 = (int) (Math.random() * (users.size())); int amount = (int)
		 * (Math.random() * (10-1)+1); sendCoin(user1, user2, amount, scroogePair);
		 * num++; }
		 */
		// System.out.println(displayBlockChain());
		/*
		 * System.out.println(lastHashPointer); System.out.println("===============");
		 * //
		 */

		/*
		 * 
		 */

	}
}
