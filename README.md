# About the Project

This is a simulation of the Scrooge blockchain system. 100 users exist, where each is given coins signed and created by scrooge. The simulation then picks a user at random and sets it as the sender. Likewise, it chooses a user at random and assigns it to be the receiver. A coin is then chosen at random from the sender's coin arraylist to be sent to the receiver. The coin transfer randomized process stop when the user clicks on the `terminate` button on the GUI. The output of the transfer process is saved in a file called `output.txt`. Note that the coin creation transactions, as well as coin transfer from Scrooge to the 100 users are eliminated from the text file to save space. 

# Classes of the Project

1-  `coin` class. This class creates a coin with a unique ID.

2- `transaction` class. This is a parent class of the 2 different types of transaction: `createCoinTransaction` and `transferCoinTransaction`. The attriutes of this class include `hash`: the hash of the transaction, `prevTransHash`: the hash of the previous transaction, `coin`: the signed coin involved in the transaction, `receiverPU`: the PUK of the user who is receving the coin in the transaction, `senderPU`: the PUK of the user who is sending the coin in the transaction.

3- `createCoinTransaction` class. This is a child class of the class `transaction`, where the sender and the receiver are the same party, Scrooge. This class is used for coin creation transactions.

4- `transferCoinTransaction` class. This is a child class of the class `transaction`, where the sender and the receiver are not the same party. The class is used for coin transfer transactions

5- `block` class. This is a class representing a block in the blockchain. It has attributes `blockID`, `transactions`: which is an arraylist of transactions in the block,  `hash`: represents the hash of the block, and`previousBlockHash`: represents the hash of the previous block.

6- `user` class. This class is used to represent the user instance in the system. It has 3 main attributes: `id`: which represents the user ID, `PUkey`: which represents the user's PUK, and the `PRkey` which represnts the user's PRK. **Note, that it is intended not to store the coins associated with the user inside this class to avoid them having to duplicate the signed coins they received, thus making the system more secure.** Therefore, the coins associated with each user are stored in class `Scrooge`.

7- `Scrooge` class. This class is used to represent Scrooge. It has many essential attributes and methods. Let's start by discussing the attributes. `lastHashPointer`: this attribute is used to stored the signed hash of the latest block, `id`: this attribute represents the ID of scrooge, `HashMap <Integer, SignedObject> signedcoins`: this hashmap is used to store the coin ID as a key and the corresponding signed verified transaction it currently exists in, `publicKey`: represents Scrooge public key, and `privatekey`: represents Scrooge private key. Finally, `HashMap <PublicKey ,  ArrayList<SignedObject>> users_coins` is a hashmap used to store the coins of each user. The key of this hashmap is the user's PUK and the value is an arraylist of `SignedObject` of the coins owned by that user (This helps in making sure that the users cannot modify the coins). 

There are also important methods in `Scrooge` class. First, the `verifySignature(SignedObject message, PublicKey PUK)` is a method used to verify transactions signed by users. The second method is  `createaCoin(int coinID)` used to create coins. Finally, `isDoubleSpending(block b, SignedObject signedtrans)` is a method used to verify that no double spending occured.

8- `blockchain` class. This is where everything is combined. It is the simulation class. I'll explain the sequence logic inside the `main` method. First, the `GUI` class is instantiated. This class causes the `terminate` button to be displayed upon. Secondly, a key pair is generated for scrooge, using the `getKeyPair()` method. Next, a `Scrooge` instance is created using the key pair generated. Fourthly, the `init(Scrooge scrooge)` method is called. This method creates coin using the `createCoins(Scrooge scrooge)` method, and instantiates users using the `createUsers(Scrooge scrooge)`, and transfers coins to them (10  for each of the 100 users).	 Finally, the randomization process starts, where a user is chosen to be the sender, and another is chosen to be the receiver. A coin is then chosen at random from the sender's balance,and sent using the `transferCoins` method. This method returns a transaction which the sender then signs using their private key with the help of the method called	`sign`. This method returns a `SignedObject` which is then sent to the `notifyScrooge` method along with the scrooge instance created previously.

`notifyScrooge`method is used to validate that the signature is indeed of the sender, the coin really belongs to the sender, and that the sender is not trying to do a double spending attack. After those 3 conditions are verified the method accumulates the transaction in a block. Once 10, transactions are accumulated the method the block is added to the blockchain. 

Please read the code comments for further clarifications.


# How to Run

To run the program go to the `blockchain` class and run. A GUI is then going to appear to allow you to terminate the program whenever you want. Make sure to let the program run for at least **30** seconds to acheive a result in the `output.txt` file. Note that I will **NOT** print the results in the console. I'll only save it in the text file.

# How to Terminate
To terminate the program, press on the `terminate` button on the GUI displayed.

