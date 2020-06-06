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

	static int blockID = 0; //holds the ID to be set for the current block in progress
	static int transID = 0; //holds the ID to be set for the current transaction in progress
	static block tempBlock = new block(-1); //the accumulator which will hold the transactions
	static LinkedList<block> blocks = new LinkedList<block>(); //the actual blockchain
	static ArrayList<user> users = new ArrayList<user>(); //the arraylist of users (including their Key and ID)

	public static String displayBlockChain(Scrooge scrooge) throws ClassNotFoundException, IOException { //A method used to display the blockchain and the last signed hash pointer (By Scrooge)
		String s = "----------------------------------------------------------------------------------------------------------BLOCKCHAIN----------------------------------------------------------------------------------------------------------"
				+ "\n";
		s += "";

		for (int i = 0; i < blocks.size(); i++) { // change to 200
			s += blocks.get(i).blockchain_member_print() + "\n";
		}
		s += "SCROOGE SIGNED HASHPOINTER: "+scrooge.lastHashPointer +"\n";
		s += "----------------------------------------------------------------------------------------------------------END BLOCKCHAIN----------------------------------------------------------------------------------------------------------"
				+ "\n";
		return s;
	}

	public static String initPrint(Scrooge scrooge) { // A method used to print the PUK of each user and the amount of coins they have
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

	public static String blockUnderConstruction(block temp) throws ClassNotFoundException, IOException { //A method used to print the block under construction
		String s = "----------------------------------------------------------------------------------------------------------Block Under Construction----------------------------------------------------------------------------------------------------------"
				+ "\n";
		s += temp.printunderConstruction();
		s += "----------------------------------------------------------------------------------------------------------END Block Under Construction----------------------------------------------------------------------------------------------------------"
				+ "\n";
		return s;
	}

	public static KeyPair getKeyPair() throws Exception { //A method used to generated PU and PR keys (key pair)
		KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA"); 
		keypairgenerator.initialize(1024, new SecureRandom());
		KeyPair private_public_key_pair = keypairgenerator.generateKeyPair();
		return private_public_key_pair;
	}

	public static SignedObject sign(Object message, PrivateKey PK) throws Exception { //A method which takes an Object of any type and signs it using the private key passed
		Signature PRsignature = Signature.getInstance("SHA1withRSA"); // create signature object
		SignedObject signedObject = new SignedObject((Serializable) message, PK, PRsignature);
		return signedObject;
	}

	public static void notifyScrooge(SignedObject signedTransaction, Scrooge scrooge) throws Exception { //A method used to add transactions to the accumulator
		MessageDigest digest = MessageDigest.getInstance("SHA-256"); //use SHA-256 to hash
		transferCoinTransaction tct = (transferCoinTransaction) signedTransaction.getObject(); //get the transaction to be added 
		boolean isVerified = scrooge.verifySignature(signedTransaction, tct.senderPU); //verify that the sender signed it using their private key
		if (isVerified) { // if that is the case
			if (scrooge.users_coins.get(tct.senderPU).size() >= 1) {//check if the user has enough balance to pay for the transaction
				int cid = ((coin) tct.coin.getObject()).id; //get the coin ID of the coin to be spent
				transaction tobechecked = (transaction) scrooge.signedcoins.get(cid).getObject(); //check the last valid transaction it was in
				if (tobechecked instanceof transferCoinTransaction) { //if the type of the transaction the coin was in last, is a transferCoinTransaction
					tobechecked = (transferCoinTransaction) scrooge.signedcoins.get(cid).getObject(); //set tobechecked to be a transaction of type transferCoinTransaction

				} else { //otherwise
					tobechecked = (createCoinTransaction) scrooge.signedcoins.get(cid).getObject();//set tobechecked to be a transaction of type createCoinTransaction
				}
				if (tobechecked.receiverPU.equals(tct.senderPU)) { //check the ownership of the coin. I.E: Is the sender of the coin, the last receiver of it?
					boolean isDoubleSpending = scrooge.isDoubleSpending(tempBlock, signedTransaction); //check if the coin is used twice (DOUBLE SPENDING ATTACK)
					if (!isDoubleSpending) { //if there is no double spending
						tempBlock.transactions.add(signedTransaction); //add the transaction to the accumulator
						// if (blocks.size() >= 200) { //change
						String blockUnderConstruction = blockUnderConstruction(tempBlock); //save the display format of the block under construction
						try { //save the block under construction in a text file called output

							PrintStream out = new PrintStream(new FileOutputStream("output.txt", true), true);
							System.setOut(out);
							out.print(blockUnderConstruction);

						} catch (IOException e) {
							System.out.println("Error during reading/writing");
						}

						// }
						if (tempBlock.transactions.size() == 10) { //if after adding the transaction, the accumulator has 10 transactions
							tempBlock.previousBlockHash = blocks.get(blocks.size() - 1).hash; //set the previous hash of the accumulator to be equal to the hash of the last block in the blockchain

							byte[] hash_block = digest.digest(tempBlock.stringify().getBytes(StandardCharsets.UTF_8)); //hash the accumulator
							String hashedBlock = Base64.getEncoder().encodeToString(hash_block); //get the string format
							tempBlock.hash = hashedBlock;//set the accumulator hash attribute to the string hash
							scrooge.lastHashPointer = sign(hashedBlock, scrooge.getPrivateKey()); //let scrooge sign the hash of the accumulator and save it
							blocks.add(tempBlock); //add the accumulator to the blockchain

							// if (blocks.size() > 200) { //change
							String blockchain = displayBlockChain(scrooge);//save the display of the blockchain
							try { //save the display to the text file "output.txt"

								PrintStream out = new PrintStream(new FileOutputStream("output.txt", true), true);
								System.setOut(out);
								out.print(blockchain);

							} catch (IOException e) {
								System.out.println("Error during reading/writing");
							}

							// }
							blockID++;//increment the blockID which is going to be given to the next accumulator
							for (int m = 0; m < tempBlock.transactions.size(); m++) { //for every transaction in the accumulator, that was just added to the blockchain:
								transferCoinTransaction trans_coinID = ((transferCoinTransaction) (tempBlock.transactions
										.get(m).getObject())); //get the transaction that was added
								coin theCoin = (coin) trans_coinID.coin.getObject(); //get the coin associated with the added transaction
								int coin_id = theCoin.id; //get the ID of that coin
								scrooge.signedcoins.put(coin_id, tempBlock.transactions.get(m)); //change the transaction in which this coin last resided to the current one
								for (int t = 0; t < scrooge.users_coins.get(trans_coinID.senderPU).size(); t++) { //go to the arraylist of coins associated with the sender, and loop on every coin
									// remove the coin with the id = coinID and put it in the receiver arraylist
									if (((coin) scrooge.users_coins.get(trans_coinID.senderPU).get(t)
											.getObject()).id == coin_id) { //if you find the coin which exists in the transaction
										SignedObject transfercoin = scrooge.users_coins.get(trans_coinID.senderPU)
												.remove(t); //remove the coin that was sent in the transaction
										scrooge.users_coins.get(trans_coinID.receiverPU).add(transfercoin); //add the coin that was sent in the transaction to the receivers coin arraylist
									}
								}

							}
							tempBlock = new block(blockID); //create a new accumulator 

						}
					} else { //if it is a double spending attack, save that to the text file along with the transaction that was involved in the attack

						try { 

							PrintStream out = new PrintStream(new FileOutputStream("output.txt", true), true);
							System.setOut(out);
							System.out.println("DOUBLE SPENDING ATTACK: ");
							out.print(tct.print());

						} catch (IOException e) {
							System.out.println("Error during reading/writing");
						}

					}
				} else {//if it is an ownership attack, save that to the text file along with the transaction that was involved in the attack

					try {

						PrintStream out = new PrintStream(new FileOutputStream("output.txt", true), true);
						System.setOut(out);
						System.out.println("OWNERSHIP ATTACK: ");
						out.print(tct.print());

					} catch (IOException e) {
						System.out.println("Error during reading/writing");
					}

				}

			} else { //if it the sender does not have enough coins to send save the following message
				try {

					PrintStream out = new PrintStream(new FileOutputStream("output.txt", true), true);
					System.setOut(out);
					System.out.println("BALANCE NOT ENOUGH!");
					out.print(tct.print());

				} catch (IOException e) {
					System.out.println("Error during reading/writing");
				}
			}
		} else { //if the signature is not verified, save the following message

			try {

				PrintStream out = new PrintStream(new FileOutputStream("output.txt", true), true);
				System.setOut(out);
				System.out.println("SIGNATURE NOT VERIFIED");
				out.print(tct.print());

			} catch (IOException e) {
				System.out.println("Error during reading/writing");
			}
			
			
			
		}
	}

	public static void createCoins(Scrooge scrooge) throws Exception { //A method used to create coins, sign them, and add them to the blockchain
		scrooge.users_coins.clear(); //make sure that the hashmap of users and their coins is empty
		scrooge.signedcoins.clear(); //make sure that the hashmap of the coins and the transactions they exist in is clear
		blockID = 0; //reset the blockID
		transID = 0; //reset the transID
		blocks.clear(); //reset the blockchain
		ArrayList<SignedObject> all_coins = new ArrayList<SignedObject>(); //create an arraylist to hold all the coins in the system
		MessageDigest digest = MessageDigest.getInstance("SHA-256"); //use SHA-256 for hashing
		int coinID = 0; //start with coinID = 0
		for (int i = 0; i < 100; i++) { //for 100 users // change replace by 100
			tempBlock = new block(blockID); //Create an accumulator which will hold the coin creation transaction (1 will exist for every user)
			for (int j = 0; j < 10; j++) { 
				coin c = new coin(coinID); //create a coin 10 x
				SignedObject signedc = sign(c, scrooge.getPrivateKey()); //sign the coin using Scrooge's private key
				createCoinTransaction cct = new createCoinTransaction(transID, signedc, scrooge.publicKey); //create a createCoinTransaction, which represents the transaction created when scrooge creates a coin
				cct.prevTransHash = null; //set the previous hash of that transaction to null, since the coin wasn't involved in any prev transaction
				byte[] hash_signed_transaction = digest.digest(cct.stringify().getBytes(StandardCharsets.UTF_8)); //hash the createCoinTransaction
				String hashedTransaction = Base64.getEncoder().encodeToString(hash_signed_transaction); //save it in a String format
				cct.hash = hashedTransaction; //set the hash of the createCoinTransaction to the string holding the hash
				SignedObject signedTrans = sign(cct, scrooge.getPrivateKey()); //sign the transaction using Scrooge's PR key
				if (scrooge.verifySignature(signedTrans, scrooge.publicKey)) { //if indeed scrooge signed the transaction
					tempBlock.transactions.add(signedTrans); //add the transaction to the accumulator
					// save the display of the block under construction
					try {

						PrintStream out = new PrintStream(new FileOutputStream("output.txt", true), true);
						System.setOut(out);
						out.print(blockUnderConstruction(tempBlock));

					} catch (IOException e) {
						System.out.println("Error during reading/writing");
					}

					coinID += 1; //increment the coin ID
					transID += 1; //increment the transaction ID
				} else { //if the sign isn't verify save the message System Attack of the text file "output.txt"
					try {

						PrintStream out = new PrintStream(new FileOutputStream("output.txt", true), true);
						System.setOut(out);
						out.print("System Attack");

					} catch (IOException e) {
						System.out.println("Error during reading/writing");
					}
				}

			}
			//after you create the 10 coins for user number i
			if (blocks.isEmpty()) { //if the blockchain is empty
				tempBlock.previousBlockHash = null; //the previous hash of the current accumulator is set to null
			} else { //otherwise
				tempBlock.previousBlockHash = blocks.get(blocks.size() - 1).hash; //set the previous hash of the current accumulator to the latest block in the blockchain
			}
			byte[] hash_block = digest.digest(tempBlock.stringify().getBytes(StandardCharsets.UTF_8)); //hash the accumulator
			String hashBlock = Base64.getEncoder().encodeToString(hash_block);//save the hash in a string format
			tempBlock.hash = hashBlock; //set the hash of the accumulator to the string hash format
			for (int k = 0; k < tempBlock.transactions.size(); k++) {//for every transaction in the accumulator
				all_coins.add(((createCoinTransaction) tempBlock.transactions.get(k).getObject()).coin); //add the coin involved in the transaction to the arraylist of all coins all_coins
				scrooge.signedcoins.put(((coin) ((createCoinTransaction) tempBlock.transactions.get(k).getObject()).coin
						.getObject()).id, ((tempBlock.transactions.get(k))));//put the coin and its corresponding transaction that it currently exists in the hashmap signedcoins
			}
			scrooge.lastHashPointer = sign(hashBlock, scrooge.getPrivateKey()); //sign the hash of the latest block and save it in a variable in class scrooge
			blocks.add(tempBlock); //add the accumulator to the blockchain
			// save the display of the blockchain in the "output.txt" file
			try {

				PrintStream out = new PrintStream(new FileOutputStream("output.txt", true), true);
				System.setOut(out);
				out.print(displayBlockChain(scrooge));

			} catch (IOException e) {
				System.out.println("Error during reading/writing");
			}

			blockID += 1; //increment the blockID
		}
		tempBlock = new block(blockID);//Create a new accumulator with the blockID
		scrooge.users_coins.put(scrooge.publicKey, all_coins); //save scrooge and all its created-signed coins in the hasmap users_coins
	}

	public static void createUsers(Scrooge scrooge) throws Exception { // A method used to create users and give them 10 coins each

		for (int i = 0; i < 100; i++) { // CHANGE //create 100 users
			int index = 0; //the index of scrooge's coin arraylist at which we will take the coin
			KeyPair kp = getKeyPair(); //Create a keypair for the user
			user u = new user(i, kp.getPrivate(), kp.getPublic()); //create a user object
			users.add(u); // add the user to the arraylist of users
			scrooge.users_coins.put(u.PUKey, new ArrayList<SignedObject>()); //inside the hashmap users_coins, put the PUK of each user and am=n empty arraylist which will hold the coins
			for (int coinAmount = 0; coinAmount < 10; coinAmount++) { //transfer 10 coins
				transferCoinTransaction t = transferCoins(scrooge.publicKey, u.PUKey, scrooge, index); //transfer the coin from scrooge to the user
				SignedObject st = sign(t, scrooge.getPrivateKey()); //sign the transaction obtained from the attempted transfer
				notifyScrooge(st, scrooge);//notify scrooge to do the checks, to make sure that the transaction is valid and can be done successfully
				index++;//increment the index at which the next coin will be fetched from scrooge list of coins 
			}
		}

	}

	public static transferCoinTransaction transferCoins(PublicKey senderPU, PublicKey receiverPU, Scrooge scrooge,
			int random) throws ClassNotFoundException, IOException, NoSuchAlgorithmException { //A method used to return a transferCoinTransaction after an attempt for a sender to send a coin to the receiver

		SignedObject transfercoin = scrooge.users_coins.get(senderPU).get(random); //get a random coin from the list of coin that the sender has to send it to the receiver
		transferCoinTransaction t = new transferCoinTransaction(transID, senderPU, receiverPU, transfercoin);//create a transferCoinTransaction using that coin, the sender, and the receiver
		if (scrooge.signedcoins.get(((coin) (transfercoin.getObject())).id)
				.getObject() instanceof transferCoinTransaction) { //if the transaction, the coin we want to send currently exists in, is a transferCoinTransaction
			transferCoinTransaction prevtrans = (transferCoinTransaction) (scrooge.signedcoins
					.get(((coin) (transfercoin.getObject())).id).getObject()); //save the transferCoinTransaction in an object prevtrans
			String previousTransactionHash = prevtrans.hash; //get the hash of that transaction
			t.prevTransHash = previousTransactionHash; // set the previous hash of the new transaction to the hash of the transaction the coin lately existed in
		} else { //if the transaction is a createCoinTransaction
			createCoinTransaction prevtrans = (createCoinTransaction) (scrooge.signedcoins
					.get(((coin) (transfercoin.getObject())).id).getObject()); //save the transaction in the prevtrans object
			String previousTransactionHash = prevtrans.hash; //save the hash of that transaction object
			t.prevTransHash = previousTransactionHash;// set the previous hash of the new transaction to the hash of the transaction the coin lately existed in
		}

		MessageDigest digest = MessageDigest.getInstance("SHA-256"); //hash using the SHA-256
		byte[] hash_signed_transaction = digest.digest(t.stringify().getBytes(StandardCharsets.UTF_8));//hash the transaction which we currently attempt to do
		String hashedTransaction = Base64.getEncoder().encodeToString(hash_signed_transaction);//convert the hash into a string
		t.hash = hashedTransaction; //set the hash of the transaction to that String
		transID += 1; //increment the transaction ID
		return t;//return the transaction, which is then signed by the sender and sent to notifyScrooge method.
	}

	public static void init(Scrooge scrooge) throws Exception { //a method which initializes the blockchain
		createCoins(scrooge); //create the coins
		createUsers(scrooge); //create the users
		String f = initPrint(scrooge); //save the display of the users and their coins
		try {

			PrintStream out = new PrintStream(new FileOutputStream("output.txt", true), true);
			System.setOut(out);
			out.print(f);

		} catch (IOException e) {
			System.out.println("Error during reading/writing");
		}
	}

	public static void main(String[] args) throws Exception {
		new GUI(); //a GUI used to terminate the code
		KeyPair scroogePair = getKeyPair(); //create a key pair for scrooge
		Scrooge scrooge = new Scrooge(scroogePair); //create Scrooge
		init(scrooge); // initialize the blockchain

		while (true) { //as long as the user does not terminate the program using the GUI

			PublicKey sender; //initialize the variable which will hold the sender's PUK
			PublicKey receiver;//initialize the variable which will hold the receiver's PUK
			Random rand = new Random(); //initialize the randomizer
			int random = rand.nextInt((99 - 0) + 1) + 0; // CHANGE, //choose a user at random from 0 - 99
			int senderIndex = random; //set that user to be the sender
			sender = users.get(senderIndex).PUKey;// get PUK of sender
			random = rand.nextInt((99 - 0) + 1) + 0; // CHANGE, choose a user at random
			while (random == senderIndex) { //make sure the chosen user is not the sender
				random = rand.nextInt((99 - 0) + 1) + 0; // CHANGE //choose a random user again if the sender = the receiver
			}
			int receiverIndex = random; //set the random user retrieved to be the receiver
			receiver = users.get(receiverIndex).PUKey; //save the receiver's PUK
			if (scrooge.users_coins.get(sender).size() >= 1) { //if the sender has enough balance
				int chosenCoin = rand.nextInt(((scrooge.users_coins.get(sender).size() - 1) - 0) + 1) + 0; //chose a coin from its balance at random to send
				transferCoinTransaction x = transferCoins(sender, receiver, scrooge, chosenCoin); //get the transaction resulting from the attempt of sending the coin from sender to receiver
				SignedObject signedTransaction = sign(x, users.get(senderIndex).getPRkey()); //sign that transaction
				notifyScrooge(signedTransaction, scrooge); //send it to scrooge for verification
			}

		}

	}
}
