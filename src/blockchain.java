import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;

public class blockchain {
	static int blockID = 1;
	static int transID = 1;
	static ArrayList<user> users = new ArrayList<user>();
	static ArrayList<transaction> transactions = new ArrayList<transaction>();
	static LinkedList<block> blocks = new LinkedList<block>();

	public static String displayBlockChain() {
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

	public static String sign(String message, PrivateKey PK) throws Exception {
		Signature PRsignature = Signature.getInstance("DSA"); // create signature object
		PRsignature.initSign(PK);
		PRsignature.update(message.getBytes(StandardCharsets.UTF_8));
		byte[] signedMessage = PRsignature.sign();
		String SM = Base64.getEncoder().encodeToString(signedMessage);
		return SM;
	}

	public static boolean verifySignature(String message, PublicKey PUK, String signedMessage) throws Exception {
		Signature PUsignature = Signature.getInstance("DSA"); // create signature object
		PUsignature.initVerify(PUK);
		PUsignature.update(message.getBytes(StandardCharsets.UTF_8));
		byte[] decodeSignedMessage = Base64.getDecoder().decode(signedMessage);
		boolean isVerified = PUsignature.verify(decodeSignedMessage);
		return isVerified;
	}

	public static void sendCoin(int senderID, int receiverID, int amount, KeyPair scroogeKeyPair) throws Exception {
		user sender = users.get((senderID));
		transaction t = new transaction(transID, receiverID, senderID, amount);
		// sign the transaction using sender's private key
		String signedTrans = sign(t.stringify(), sender.PRkey);
		// verify that the transaction came out of the sender
		boolean isVerified = verifySignature(t.stringify(), sender.PUKey, signedTrans);
		if (isVerified) { // if indeed the sender is the one who sent the message
			// check if they have enough money to do the transaction
			if (sender.coins.size() >= amount) { // if they have enough money
				// getting the transaction ready to be added to the block through hashing
				if (transactions.isEmpty()) {
					t.prevTransHash = null;
				} else {
					t.prevTransHash = transactions.get(transactions.size() - 1).hash;
				}
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash_signed_transaction = digest.digest(t.stringify().getBytes(StandardCharsets.UTF_8));
				// Convert byte[] to String
				String hashedTransaction = Base64.getEncoder().encodeToString(hash_signed_transaction);
				t.hash = hashedTransaction;
				transactions.add(t);
				transID += 1;
				// publish transaction in block
				if (blocks.isEmpty()) { // if there aren't blocks yet
					// create one
					block b = new block(blockID);
					b.previousBlockHash = null;
					b.transactions.add(t);
					blocks.add(b);
				} else if (blocks.get(blocks.size() - 1).transactions.size() == 10) { // if the block has 10
																						// transactions, create a new
					// one
					blockID++;
					block b = new block(blockID);
					b.previousBlockHash = sign(blocks.get(blocks.size() - 1).hash, scroogeKeyPair.getPrivate());
					b.transactions.add(t);
					blocks.add(b);
				} else { // if block still has places
					blocks.get(blocks.size() - 1).transactions.add(t);
					if (blocks.get(blocks.size() - 1).transactions.size() == 10) {
						// hash it
						byte[] hash_block = digest
								.digest(blocks.get(blocks.size() - 1).stringify().getBytes(StandardCharsets.UTF_8));
						// Convert byte[] to String
						String hashedBlock = Base64.getEncoder().encodeToString(hash_block);
						blocks.get(blocks.size() - 1).hash = hashedBlock;
					}
				}
				// do actual deduction and additions to the coins arraylist
				user receiver = users.get(receiverID);
				for (int i = 0; i < amount; i++) {
					receiver.coins.add(sender.coins.remove(0));
				}

			} else {
				System.out.println("Hey sender #" + senderID + "! Your balance is not enough." + "You only have "
						+ sender.coins.size() + " coins.");
			}
		} else {
			System.out.println("Not Verified");
		}
	}

	public static void createCoins(KeyPair scroogePair) throws Exception {
		users.clear();
		blockID = 1;
		transID = 1;
		blocks.clear();
		transactions.clear();
		int coinID = 1;
		for (int i = 0; i < 100; i++) {
			int userId = i + 1;
			KeyPair userPair = getKeyPair();
			user userA = new user(userId, userPair.getPrivate(), userPair.getPublic());
			for (int c = 0; c < 10; c++) {
				coin theCoin = new coin(coinID);
				coinID += 1;
				String Signedcoin = sign(theCoin.stringify(), scroogePair.getPrivate()); // sign the coin
				userA.coins.add(Signedcoin);// add the coin to the user's arraylist of coins
			}
			users.add(userA);
		}
	}

	public static void main(String[] args) throws Exception {
		KeyPair scroogePair = getKeyPair();
		createCoins(scroogePair);
		/*
		 * int num = 0; while (num < 20) { int user1 = (int) Math.random() *
		 * (users.size()); int user2 = (int) Math.random() * (users.size()); int amount
		 * = (int) Math.random() * (users.size()-1)+1; sendCoin(user1, user2, amount,
		 * scroogePair); num++; }
		 */

		sendCoin(0, 1, 5, scroogePair);
		sendCoin(0, 2, 5, scroogePair);
		System.out.println(displayBlockChain());
	}
}
