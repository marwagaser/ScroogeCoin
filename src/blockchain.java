import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
import java.util.Map.Entry;
import java.util.Random;

public class blockchain {
	static SignedObject lastHashPointer;
	static int blockID = 0;
	static int transID = 0;
	static block tempBlock = new block(-1);
	static ArrayList<SignedObject> all_transactions = new ArrayList<SignedObject>();
	static LinkedList<block> blocks = new LinkedList<block>();
	static ArrayList<user> users = new ArrayList<user>();

	public static String displayBlockChain() throws ClassNotFoundException, IOException {
		String s = "----------------------------------------------------------------------------------------------------------BLOCKCHAIN----------------------------------------------------------------------------------------------------------"
				+ "\n";
		s += "";

		for (int i = 200; i < blocks.size(); i++) {
			s += blocks.get(i).print() + "\n";
		}
		s += "----------------------------------------------------------------------------------------------------------END BLOCKCHAIN----------------------------------------------------------------------------------------------------------"
				+ "\n";
		return s;
	}

	public static String initPrint(Scrooge scrooge) {
		String s = "----------------------------------------------------------------------------------------------------------Users and Coins----------------------------------------------------------------------------------------------------------"
				+ "\n";
		s += "Users' Public Keys and Coins:" + "\n";
		for (Entry<PublicKey, ArrayList<SignedObject>> me : scrooge.users_coins.entrySet()) {
			if (!me.getKey().equals(scrooge.publicKey)) {
				s += "User Public Key: " + me.getKey() + " & Coins: " + me.getValue().size() + "\n";
			}
		}
		s += "---------------------------------------------------------------------------------------------------------- END Users and Coins----------------------------------------------------------------------------------------------------------"
				+ "\n";
		return s;
	}

	public static String blockUnderConstruction(block temp) throws ClassNotFoundException, IOException {
		String s = "----------------------------------------------------------------------------------------------------------Block Under Construction----------------------------------------------------------------------------------------------------------"
				+ "\n";
		s += temp.printunderConstruction();
		s += "----------------------------------------------------------------------------------------------------------END Block Under Construction----------------------------------------------------------------------------------------------------------"
				+ "\n";
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

	public static void notifyScrooge(SignedObject signedTransaction, PublicKey senderPUK, Scrooge scrooge)
			throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		// VERIFY THAT THE USER SIGNED THE TRANSACTION USING THEIR PRIVATE KEY
		boolean isVerified = scrooge.verifySignature(signedTransaction, senderPUK);
		if (isVerified) { // if that is the case

			if (scrooge.users_coins.get(senderPUK).size() >= 1) {// if scrooge actually has the money to pay for the
																	// transaction
				// check for double spending by scrooge

				// check ownership

				transferCoinTransaction tct = (transferCoinTransaction) signedTransaction.getObject();
				SignedObject cid = tct.coin;
				transaction tobechecked = (transaction) scrooge.signedcoins.get(((coin) cid.getObject()).id)
						.getObject();
				// System.out.println(((coin) cid.getObject()).id);
				if (tobechecked.receiverPU.equals(senderPUK)) { // CHECK OWNERSHIP
					boolean isDoubleSpending = scrooge.isDoubleSpending(tempBlock, signedTransaction);
					if (!isDoubleSpending) {

						tempBlock.transactions.add(signedTransaction);
						if (blocks.size() >= 200) {
							String blockUnderConstruction = blockUnderConstruction(tempBlock);
							System.out.println(blockUnderConstruction);
						}
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
							lastHashPointer = sign(hashedBlock, scrooge.getPrivateKey());
							blocks.add(tempBlock);

							if (blocks.size() > 200) {
								String blockchain = displayBlockChain();
								System.out.println(blockchain);
							}

							blockID++;
							for (int k = 0; k < tempBlock.transactions.size(); k++) {
								scrooge.signedcoins
										.replace(
												((coin) ((transferCoinTransaction) tempBlock.transactions.get(k)
														.getObject()).coin.getObject()).id,
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

							tempBlock = new block(blockID);

						}
					} else {
						System.out.println("ownership verification failed");
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
		ArrayList<SignedObject> all_coins = new ArrayList<SignedObject>();
		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		// create blocks for the blockchain and publish them after 10 transactions
		int coinID = 0;
		for (int i = 0; i < 100; i++) { // changed - replace 1 by 100
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
				cct.receiverPU = scrooge.publicKey;
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
				all_coins.add(((createCoinTransaction) tempBlock.transactions.get(k).getObject()).coin);
				scrooge.signedcoins.put(((coin) ((createCoinTransaction) tempBlock.transactions.get(k).getObject()).coin
						.getObject()).id, ((tempBlock.transactions.get(k))));
			}
			lastHashPointer = sign(hashBlock, scrooge.getPrivateKey());
			blocks.add(tempBlock);
			blockID += 1;
		}
		tempBlock = new block(blockID);
		scrooge.users_coins.put(scrooge.publicKey, all_coins);
	}

	public static void createUsers(Scrooge scrooge) throws Exception {

		for (int i = 0; i < 100; i++) {
			int index = 0;
			KeyPair kp = getKeyPair();
			user u = new user(i, kp.getPrivate(), kp.getPublic());
			users.add(u); // an arraylist of users
			scrooge.users_coins.put(u.PUKey, new ArrayList<SignedObject>());
			for (int coinAmount = 0; coinAmount < 10; coinAmount++) {
				SignedObject st = sign(transferCoins(scrooge.publicKey, u.PUKey, scrooge, index),
						scrooge.getPrivateKey());
				notifyScrooge(st, scrooge.publicKey, scrooge);
				index++;
			}

		}

	}

	public static transferCoinTransaction transferCoins(PublicKey senderPU, PublicKey receiverPU, Scrooge scrooge,
			int random) throws ClassNotFoundException, IOException, NoSuchAlgorithmException {

		SignedObject transfercoin = scrooge.users_coins.get(senderPU).get(random);
		transferCoinTransaction t = new transferCoinTransaction(transID, senderPU, receiverPU, transfercoin);
		if (scrooge.signedcoins.get(((coin) (transfercoin.getObject())).id)
				.getObject() instanceof transferCoinTransaction) {// get the place
			// where the current
			// coin exists
			transferCoinTransaction prevtrans = (transferCoinTransaction) scrooge.signedcoins
					.get(((coin) (transfercoin.getObject())).id).getObject();
			String previousTransactionHash = prevtrans.hash;
			t.prevTransHash = previousTransactionHash; // set the previous hash of the new transaction to that previous
			// transaction
		} else {
			createCoinTransaction prevtrans = (createCoinTransaction) scrooge.signedcoins
					.get(((coin) (transfercoin.getObject())).id).getObject();
			String previousTransactionHash = prevtrans.hash;
			t.prevTransHash = previousTransactionHash; // set the previous hash of the new transaction to that previous
		}

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash_signed_transaction = digest.digest(t.stringify().getBytes(StandardCharsets.UTF_8));
		// Convert byte[] to String
		String hashedTransaction = Base64.getEncoder().encodeToString(hash_signed_transaction);
		t.hash = hashedTransaction;
		transID += 1;
		return t;
		// sign and add to the transactions array and notify scrooge
	}

	public static void init(Scrooge scrooge) throws Exception {
		createCoins(scrooge);
		createUsers(scrooge);
		String f = initPrint(scrooge);
		System.out.println(f);
	}

	public static void main(String[] args) throws Exception {
/*		new GUI();
		KeyPair scroogePair = getKeyPair();
		Scrooge scrooge = new Scrooge(scroogePair);
		init(scrooge);*/
		
		/*
		 * for (Entry<Integer, SignedObject> me : scrooge.signedcoins.entrySet()) {
		 * System.out.println(me.getKey()+" "+me.getValue()); }
		 */
/*		while (true) {

			PublicKey sender;
			PublicKey receiver;
			Random rand = new Random();
			int random = rand.nextInt((99 - 0) + 1) + 0;
			int senderIndex = random;
			sender = users.get(senderIndex).PUKey;// get PUK of sender
			random = rand.nextInt((99 - 0) + 1) + 0;
			while (random == senderIndex) {
				random = rand.nextInt((99 - 0) + 1) + 0;
			}
			int receiverIndex = random;
			receiver = users.get(receiverIndex).PUKey;
			int chosenCoin = rand.nextInt(((scrooge.users_coins.get(sender).size() - 1) - 0) + 1) + 0;
			transferCoinTransaction x = transferCoins(sender, receiver, scrooge, chosenCoin);
			SignedObject signedTransaction = sign(x, users.get(senderIndex).getPRkey());
			notifyScrooge(signedTransaction, sender, scrooge);
		}*/
		try {
			
			PrintStream out = new PrintStream(
			        new FileOutputStream("output.txt", true), true);
			System.setOut(out);
			out.print("your output");
		
		}
		catch(IOException e){
			   System.out.println("Error during reading/writing");
		}
		
	}
}
