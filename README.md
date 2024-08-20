# Pharmacy Messaging App
README


Instructions to Compile and Run:

1. Compilation:
    - Navigate to the directory containing your Java source files (.java files).
    - Open a terminal or command prompt in that directory.
    - Compile the files using the javac command.

2. Running:
    - After successful compilation, run the server class (MessagingServer in this case) using the java command.
    - Then, run the client class (PharmacyApp in this case) using the java command.


Description of each Class:


1. Product Class:

- Functionality:
    - Represents a product within the online marketplace.
    - Has attributes for product name and quantity.
    - Provides methods to set and get the quantity of the product.
    - Implements Serializable for potential serialization.

- Methods:
- setQuantity(int quantity): Sets the quantity of the product.
- getProductName(): Retrieves the product name.
- getQuantity(): Retrieves the quantity of the product.


- Testing:
    - Methods were tested by following the test case instructions in the TestCasesProject5 file.

- Relationship to Other Classes:
    - Associated with the Pharmacy class. Products are part of a store's inventory. Methods are used in PharmacyApp and MessagingServer classes.

2. Pharmacy Class:

- Functionality:
    - Represents a virtual store in the online marketplace.
    - Contains a name, a seller (User), and a list of products.
    - Allows adding products, finding products by name, and checking product existence.
    - Implements Serializable for potential serialization.

- Methods:
- addProduct(Product newProduct): Adds a new product to the store.
- findProductByName(String productName): Finds a product in the store by name.
- hasProduct(Product product): Checks if a specific product exists in the store.

- Testing:
    - Methods were tested by following the test case instructions in the TestCasesProject5 file.


- Relationship to Other Classes:
    - Associated with the Product and User classes. A store has products and is owned by a user (seller). Methods are used in PharmacyApp and MessagingServer classes.



3. User Class:

- Functionality:
    - Represents a user in the online marketplace.
    - Contains attributes for username, email, password, role, messages, blocked users, and stores.
    - Provides methods for blocking users, sending/receiving messages, managing stores, and updating user information.
    - Implements Serializable for potential serialization.

- Methods
- blockUser(User userToBlock): Blocks a specified user.
- addPharmacys(Pharmacy store): Adds a store to the list of stores owned by the user.
- getPharmacys(): Retrieves the list of stores owned by the user.
- sendMessage(User recipient, String content): Sends a message to another user.
- receiveMessage(Message message): Receives a message from another user.
- editMessage(Message message, String newContent): Edits a message sent by the user.
- deleteMessage(Message message): Deletes a message sent by the user.
- removeMessage(Message message): Removes a message from the user's message list.
- setUsername(String newUsername): Sets a new username for the user.
- setEmail(String newEmail): Sets a new email for the user.
- setPassword(String newPassword): Sets a new password for the user.

- Testing:
    - Methods were tested by following the test case instructions in the TestCasesProject5 file.

- Relationship to Other Classes:
    - Associated with the Pharmacy, Message, and other User classes. Users can send messages, block other users, and own stores. Methods used in PharmacyApp and MessagingServer classes.



4. Message Class:

- Functionality:
    - Represents a message within the messaging system.
    - Contains details such as sender, recipient, content, and timestamp.
    - Allows editing and deleting messages.
    - Implements Serializable for potential serialization.

- Methods:
- setContent(String newContent): Sets a new content for the message.

- Testing:
    - Methods were tested by following the test case instructions in the TestCasesProject5 file.

- Relationship to Other Classes:
    - Primarily used by the User class for message-related operations. Methods used in PharmacyApp and MessagingServer classes.





5. MessagingServer Class:

- Functionality:
    - Represents the server side of the main controller for the messaging system.
    - Manages user input, navigation, and interactions with other classes, received from PharmacyApp, and sends back data correspondingly.
    - Implements Serializable for potential serialization.
    - Connects all the other classes together

- Methods
- userOut(Map dataList): Writes the user data to a file named "Users.txt" using serialization. Takes a Map of usernames and corresponding User objects.

- userIn(): Reads user data from the "Users.txt" file using deserialization. Returns a Map of usernames and corresponding User objects.

- createUser(String username, String email, String password, String role): Creates a new User object and adds it to the users map. Takes parameters for username, email, password, and role.

- sendObject(User user): Sends user object to client.

- main(String[] args): The main method that serves as the entry point for the messaging system.
  Manages the overall flow of the program, user authentication, and menu navigation.


- Testing:
    - Methods were tested by following the test case instructions in the TestCasesProject5 file.


- Relationship to Other Classes:
    - Sends back data to PharmacyApp depending on the data it receives (user input).




6. PharmacyApp Class:

- Functionality:
    - Provides a menu-driven interface for user interactions.
    - Manages user input, navigation, and interactions with other classes, sends to MessagingServer, and receives back data correspondingly.
    - Implements Serializable for potential serialization.
    - Connects all the other classes together

- Methods:
- userOut(Map dataList): Writes the user data to a file named "Users.txt" using serialization. Takes a Map of usernames and corresponding User objects.

- userIn(): Reads user data from the "Users.txt" file using deserialization. Returns a Map of usernames and corresponding User objects.
  
- importPharmacys(Map dataList): Imports stores associated with each user from the Map of users. Returns an ArrayList of imported Pharmacy objects.
  
- findPharmacyByName(String storeName): Finds and returns a Pharmacy object based on the provided store name.
  
- sendMessage(User recipient, String content): Allows the user to send a message to another user. Handles different roles (seller to customer, customer to seller).
  
- createUser(): Guides the user through the process of creating a new account. Takes input for username, email, password, and role.
  
- deleteAccountCustomerPanel(): Allows the customer to delete their account.
  
- editAccountCustomerPanel(): Allows the customer to edit their account information.
  
- blockUserCustomerPanel(): Allows the customer to block a seller.
  
- searchPharmacistPanel(): Guides the user to search for a seller and send them a message.
  
- viewPharmacistPanel(): Displays available stores and allows the user to send a message to a seller. Provides options to send a text message or import a message from a file.
  
- exportFileCustomerPanel(): Allows the customer to export their messages to a CSV file.
  
- editPanelCustomerPanel(): Allows the customer to edit the content of one of their messages.
  
- deleteMessageCustomerPanel(): Allows the customer to delete one of their messages.
  
- viewingMessagesCustomerPanel(): Displays the messages of the logged-in customer.
  
- inputFileCustomerPanel(): Allows the customer to import a message from a text file.
  
- sendMessageCustomerPanel(): Allows the customer to send a message to a pharmacist.
  
- buyMedicinePanel(): Allows the logged-in user (customer) to purchase a product from a store. Takes input for store name, product name, and quantity.
  
- deleteAccountSellerPanel(): Allows the seller to delete their account.
  
- editAccountSellerPanel(): Allows the seller to edit their account information.
  
- blockPatientPanel(): Allows the seller to block a patient.
  
- viewPatientPanel(): Displays available patients and allows the user to send a message to a patient.
  
- searchPatientPanel(): Guides the user to search for a patient and send them a message.
  
- deleteMessageSellerPanel(): Allows the seller to delete one of their messages.
  
- editMessagesSellerPanel(): Allows the seller to edit the content of one of their messages.
  
- exportFileSellerPanel(): Allows the seller to export their messages to a CSV file.
  
- viewingMessagesSellerPanel(): Displays the messages of the logged-in seller.
  
- sendMessageSellerPanel(): Allows the seller to send a message to a pharmacist.
  
- inputFileSellerPanel(): Allows the seller to import a message from a text file.
  
- addProductPanel(): Allows the logged-in user (seller) to add a product to their store. Takes input for store name, product name, and quantity.
  
- createStorePanel(): Allows the logged-in user (seller) to create a new store. Takes input for the store name.
  
- createLoginPanel(): Guides the user through the login process. Takes input for username and password.
  
- createAccountPanel(): Creates a new User object and adds it to the users map. Takes parameters for username, email, password, and role.
  
- viewMessagesSellerPanel(): Displays the messages of the logged-in seller.
  
- viewMessagesCustomerPanel(): Displays the messages of the logged-in customer.
  
- createMessagingPanelSeller(): Gives the seller options to send a message to a patient.
  
- createMessagingPanelCustomer(): Gives the customer options to send a message to a pharmacist.
  
- sellerPanel(): Shows menu options that only sellers are able to access.
  
- customerPanel(): Shows menu options that only customers are able to access.
  
- createEditPanelSeller(): Allows the customer to either block user or edit/delete account.
  
- createEditPanelCustomer(): Allows the customer to either block user or edit/delete account.
  
- serverSend(String message): Sends information dependent on user input to the server class.
  
- serverReceive(): Receives information dependent on user input from the server class.
  
- setupNetworking(): Connects PharmacyApp to MessagingServer to set up a client-server base.
  
- main(String[] args): The main method that serves as the entry point for the messaging system.
  Manages the overall flow of the program, user authentication, and menu navigation.


- Testing:
    - Methods were tested by following the test case instructions in the TestCasesProject5 file.


- Relationship to Other Classes:
  - Orchestrates the interaction between users, stores, and messages. Utilizes the functionality provided by other classes.


