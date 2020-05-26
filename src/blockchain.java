import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	static int blockID = 0;
	static int transID = 0;
	static block tempBlock = new block(-1);
	static ArrayList<SignedObject> all_transactions = new ArrayList<SignedObject>();
	static LinkedList<block> blocks = new LinkedList<block>();
	static ArrayList<user> users = new ArrayList<user>();

	public static String displayBlockChain() throws ClassNotFoundException, IOException {
		String s = "";
		for (int i = 0; i < blocks.size(); i++) {
			s += blocks.get(i).print() + "\n";
		}
		return s;
	}

	public static String displayUsers(Scrooge s) {
		return s.print();
	}

	public static KeyPair getKeyPair() throws Exception {
		KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("DSA"); // use DSA to generate PU-PR key pair
		keypairgenerator.initialize(1024, new SecureRandom());
		KeyPair private_public_key_pair = keypairgenerator.generateKeyPair();
		return private_public_key_pair;
	}

	public static SignedObject sign(Object message, PrivateKey PK) throws Exception {
		Signature PRsignature = Signature.getInstance("DSA"); // create signature object
		SignedObject signedObject = new SignedObject((Serializable) message, PK, PRsignature);
		return signedObject;
	}

	public static void notifyScrooge(SignedObject signedTransaction, PublicKey senderPUK, Scrooge scrooge) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		// VERIFY THAT THE USER SIGNED THE TRANSACTION USING THEIR PRIVATE KEY
		boolean isVerified = scrooge.verifySignature(signedTransaction, senderPUK);
		if (isVerified) { // if that is the case
			if (scrooge.users_coins.get(senderPUK).size() >= 1) {// if scrooge actually has the money to pay for the
																	// transaction
				// Add to block
				// check for double spending by scrooge

				boolean isDoubleSpending = scrooge.isDoubleSpending(tempBlock, signedTransaction);
				if (!isDoubleSpending) {
					tempBlock.transactions.add(signedTransaction);

					// and check if now we are 10 in the block
					if (tempBlock.transactions.size() == 10) {
						tempBlock.previousBlockHash = blocks.get(blocks.size() - 1).hash;

						byte[] hash_block = digest
								.digest(blocks.get(blocks.size() - 1).stringify().getBytes(StandardCharsets.UTF_8)); // hash
						// the
						// block
						// Convert byte[] to String
						String hashedBlock = Base64.getEncoder().encodeToString(hash_block);
						tempBlock.hash = hashedBlock;
						lastHashPointer = hashedBlock;
						blocks.add(tempBlock);
						blockID++;
						for (int k = 0; k < tempBlock.transactions.size(); k++) {
							scrooge.signedcoins.put(
									((transferCoinTransaction) tempBlock.transactions.get(k).getObject()).coin,
									((tempBlock.transactions.get(k))));
							// actually do the transfer

							transferCoinTransaction currentTrans = ((transferCoinTransaction) tempBlock.transactions
									.get(k).getObject());
							int coinID = ((coin) currentTrans.coin.getObject()).id;
							for (int t = 0; t < scrooge.users_coins.get(senderPUK).size(); t++) {
								// remove the coin with the id = coinID and put it in the receiver arraylist
								if (((coin) scrooge.users_coins.get(senderPUK).get(t).getObject()).id == coinID) {
									// REMOVE IT AND ADD IT TO RECEIVER
									SignedObject transfercoin = scrooge.users_coins.get(senderPUK).remove(t);
									scrooge.users_coins.get(currentTrans.receiverPU).add(transfercoin);
									break;
								}
								//
							}

						}
						// actual deduction
						tempBlock = new block(blockID);
						// create a new block
					}
				}

			} else {
				System.out.println("You do not have enough money");
			}
		} else {
			System.out.println("ATTACK");
		}
	}

	public static void createCoins(Scrooge scrooge) throws Exception {
		scrooge.users_coins.clear();
		blockID = 0;
		transID = 0;
		blocks.clear();
		all_transactions.clear();
		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		// create blocks for the blockchain and publish them after 10 transactions
		int coinID = 0;
		for (int i = 0; i < 100; i++) {
			tempBlock = new block(blockID); // create a new temp block for each user (100)
			for (int j = 0; j < 10; j++) {
				coin c = new coin(coinID);
				SignedObject signedc = sign(c, scrooge.getPrivateKey());
				createCoinTransaction cct = new createCoinTransaction(transID, signedc); // transaction
																							// to
																							// send
																							// from
																							// scrooge
																							// to
																							// user
																							// i,
																							// 1
																							// coin

				cct.prevTransHash = null;
				byte[] hash_signed_transaction = digest.digest(cct.stringify().getBytes(StandardCharsets.UTF_8));
				// Convert byte[] to String
				String hashedTransaction = Base64.getEncoder().encodeToString(hash_signed_transaction);
				cct.hash = hashedTransaction;
				// sign the transaction using sender's private key
				SignedObject signedTrans = sign(cct, scrooge.getPrivateKey());
				if (scrooge.verifySignature(signedTrans, scrooge.publicKey)) {
					tempBlock.transactions.add(signedTrans);
					tempBlock.previousBlockHash = null;
					coinID += 1;
					transID += 1;
				} else {
					System.out.println("System Attack!");
				}

			}
			if (blocks.isEmpty()) {
				tempBlock.previousBlockHash = null;
			} else {
				tempBlock.previousBlockHash = blocks.get(blocks.size() - 1).hash;
			}
			byte[] hash_block = digest.digest(tempBlock.stringify().getBytes(StandardCharsets.UTF_8));
			// Convert byte[] to String
			String hashBlock = Base64.getEncoder().encodeToString(hash_block);
			tempBlock.hash = hashBlock;
			for (int k = 0; k < tempBlock.transactions.size(); k++) {
				scrooge.signedcoins.put(((createCoinTransaction) tempBlock.transactions.get(k).getObject()).coin,
						((tempBlock.transactions.get(k))));
			}

			blocks.add(tempBlock);
			blockID += 1;
		}
		tempBlock = new block(blockID);
		ArrayList<SignedObject> coins = new ArrayList<>(scrooge.signedcoins.keySet());
		scrooge.users_coins.put(scrooge.publicKey, coins);
	}

	public static void createUsers(Scrooge scrooge) throws Exception {
		for (int i = 0; i < 2; i++) {
			KeyPair kp = getKeyPair();
			user u = new user(i, kp.getPrivate(), kp.getPublic());
			users.add(u); // an arraylist of users
			scrooge.users_coins.put(u.PUKey, new ArrayList<SignedObject>());
			SignedObject st = sign(transferCoins(scrooge.publicKey, u.PUKey, scrooge), scrooge.getPrivateKey());
			notifyScrooge(st, scrooge.publicKey, scrooge);
		}

	}

	public static transaction transferCoins(PublicKey senderPU, PublicKey receiverPU, Scrooge scrooge)
			throws ClassNotFoundException, IOException, NoSuchAlgorithmException {
		int rand = 0;
		SignedObject transfercoin = scrooge.users_coins.get(senderPU).get(rand);
		transferCoinTransaction t = new transferCoinTransaction(transID, senderPU, receiverPU, transfercoin);
		if (scrooge.signedcoins.get(transfercoin).getObject() instanceof transferCoinTransaction) {// get the place
																									// where the current
																									// coin exists
			transferCoinTransaction prevtrans = (transferCoinTransaction) scrooge.signedcoins.get(transfercoin)
					.getObject();
			String previousTransactionHash = prevtrans.hash;
			t.prevTransHash = previousTransactionHash; // set the previous hash of the new transaction to that previous
			// transaction
		} else {
			createCoinTransaction prevtrans = (createCoinTransaction) scrooge.signedcoins.get(transfercoin).getObject();
			String previousTransactionHash = prevtrans.hash;
			t.prevTransHash = previousTransactionHash; // set the previous hash of the new transaction to that previous
		}

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash_signed_transaction = digest.digest(t.stringify().getBytes(StandardCharsets.UTF_8));
		// Convert byte[] to String
		String hashedTransaction = Base64.getEncoder().encodeToString(hash_signed_transaction);
		t.hash = hashedTransaction;
		return t;
		// sign and add to the transactions array and notify scrooge
	}

	public static void init(Scrooge scrooge) throws Exception {
		createCoins(scrooge);
		createUsers(scrooge);
	}

	public static void main(String[] args) throws Exception {
		KeyPair scroogePair = getKeyPair();
		Scrooge scrooge = new Scrooge(scroogePair);
		init(scrooge);
		System.out.println(scrooge.users_coins.size());
/*System.out.println(scrooge.users_coins);*/
	//  SignedObject trans = sign(transferCoins(users.get(0).PUKey, users.get(1).PUKey, scrooge),users.get(0).getPRkey());
	//  SignedObject trans1 = sign(transferCoins(users.get(0).PUKey, users.get(5).PUKey, scrooge),users.get(0).getPRkey());
		/*transferCoins(users.get(0).PUKey, users.get(3).PUKey, scrooge);
		transferCoins(users.get(1).PUKey, users.get(2).PUKey, scrooge);
		transferCoins(users.get(2).PUKey, users.get(3).PUKey, scrooge);
		transferCoins(users.get(3).PUKey, users.get(4).PUKey, scrooge);
		transferCoins(users.get(4).PUKey, users.get(5).PUKey, scrooge);
		transferCoins(users.get(5).PUKey, users.get(6).PUKey, scrooge);
		transferCoins(users.get(6).PUKey, users.get(7).PUKey, scrooge);
		transferCoins(users.get(7).PUKey, users.get(8).PUKey, scrooge);
		transferCoins(users.get(8).PUKey, users.get(9).PUKey, scrooge);*/

	}
}
