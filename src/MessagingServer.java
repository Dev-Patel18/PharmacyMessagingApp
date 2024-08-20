import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagingServer {

    public static Map<String, User> users = new HashMap<>();
    public static ArrayList<Pharmacy> pharmacys = new ArrayList<>();
    private static final Object gateKeeper = new Object();

    public static void main(String[] args) {
        int portNumber = 12345; // Choose a port number


        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server is running. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a thread to handle each client
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createUser(String username, String email, String password, String role) {
        User user = new User(username, email, password, role);
        users.put(username, user);
    }

    /**
     * Reads user data from the file ("Users.txt") using ObjectInputStream and returns a map.
     * Reads user objects from the file and populates a map with usernames as keys.
     */
    private static Map<String, User> userIn() {
        Map<String, User> userData = new HashMap<>();
        try {
            File f = new File("Userss.txt");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            User u = (User) ois.readObject();
            while (u != null) {
                userData.put(u.getUsername(), u);
                u = (User) ois.readObject();
            }

        } catch (EOFException e) {
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return userData;
    }

    /**
     * Saves the user data to a file ("Users.txt") using ObjectOutputStream.
     * Iterates over the user data map and writes each user object to the file.
     */
    private static void userOut(Map<String, User> dataList) {
        try {
            File f = new File("Userss.txt");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            for (String keys : dataList.keySet()) {
                User u = dataList.get(keys);
                oos.writeObject(u);
            }
            oos.close();
        } catch (IOException e) {
        }
    }

    private static ArrayList<Pharmacy> importPharmacys(Map<String, User> dataList) {

        ArrayList<Pharmacy> importedPharmacys = new ArrayList<>();
        List<Pharmacy> userPharmacys = new ArrayList<>();
        for (User user : dataList.values()) {
            userPharmacys = user.getPharmacys();
            importedPharmacys.addAll(userPharmacys);
        }
        return importedPharmacys;
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        private static User currentUser;



        @Override
        public void run() {
            try (
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                users = userIn();
                String inputLine;
                inputLine = in.readLine();
                while (inputLine != null) {
                    System.out.println("Received from client: " + inputLine);
                    String[] lines = inputLine.split(",");

                    if (lines[0].equals("1")) {
                        synchronized (gateKeeper) {
                            String username = lines[1];
                            String password = lines[2];
                            if (!users.containsKey(username)) {
                                out.write("1");
                                out.println();
                                out.flush();
                            } else if (!users.get(username).password.equals(password)) {
                                out.write("2");
                                out.println();
                                out.flush();
                            } else if ((users.get(username).password.equals(password)) && (users.get(username).getRole().equals("Patient"))) {
                                out.write("3");
                                out.println();
                                out.flush();
                                currentUser = users.get(username);
                            } else if ((users.get(username).password.equals(password)) && (users.get(username).getRole().equals("Pharmacist"))) {
                                out.write("4");
                                out.println();
                                out.flush();
                                currentUser = users.get(username);
                            }
                        }
                    }

                    if (lines[0].equals("2")) {
                        synchronized (gateKeeper) {
                            String username = lines[1];
                            String email = lines[2];
                            String password = lines[3];
                            String role = lines[4];

                            if ((users.containsKey(username))) {
                                out.write("1");
                                out.println();
                                out.flush();
                            } else if ((users.containsKey(email))) {
                                out.write("2");
                                out.println();
                                out.flush();
                            } else {
                                createUser(username, email, password, role);
                                out.write("3");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("3")) {
                        synchronized (gateKeeper) {
                            String recipient = lines[1];
                            String content = lines[2];
                            if (users.containsKey(recipient)) {
                                User receiver = users.get(recipient);
                                if (currentUser.isUserBlocked(receiver)) {
                                    out.write("1");
                                    out.println();
                                    out.flush();
                                } else if (receiver.isUserBlocked(currentUser)) {
                                    out.write("2");
                                    out.println();
                                    out.flush();
                                } else if (currentUser.getRole().equals("Pharmacist") && receiver.getRole().equals("Patient")) {
                                    Message message = new Message(currentUser, receiver, content);
                                    currentUser.receiveMessage(message);
                                    receiver.receiveMessage(message);
                                    out.write("3");
                                    out.println();
                                    out.flush();
                                } else if (currentUser.getRole().equals("Patient") && receiver.getRole().equals("Pharmacist")) {
                                    Message message = new Message(currentUser, receiver, content);
                                    currentUser.receiveMessage(message);
                                    receiver.receiveMessage(message);
                                    out.write("4");
                                    out.println();
                                    out.flush();
                                } else {
                                    out.write("5");
                                    out.println();
                                    out.flush();
                                }
                            } else {
                                out.write("6");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("4")) {
                        synchronized (gateKeeper) {
                            if (currentUser != null) {
                                List<Message> userMessages = currentUser.getMessages();
                                String messages = "";
                                if (!userMessages.isEmpty()) {
                                    String messagesString = "";
                                    for (Message message : userMessages) {
                                        messagesString += message.getSenderName() + ": " + message.getContent() + ",";
                                    }
                                    messages += messagesString;
                                } else {
                                    messages += "No messages.";
                                }
                                out.write(messages);
                                out.println();
                                out.flush();
                            } else {
                                out.write("void");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("5")) {
                        synchronized (gateKeeper) {
                            String blocked = lines[1];
                            User blockUser = users.get(blocked);

                            if (blocked.isEmpty()) {
                            } else {
                                if (blockUser == null) {
                                    out.write("1");
                                    out.println();
                                    out.flush();
                                } else {
                                    currentUser.blockUser(blockUser);
                                    out.write("2");
                                    out.println();
                                    out.flush();
                                }
                            }
                        }
                    }

                    if (lines[0].equals("6")) {
                        synchronized (gateKeeper) {
                            String messageContent = lines[1];
                            String newContent = lines[2];
                            int c = 0;
                            for (Message message : currentUser.getMessages()) {
                                if (message.getContent().equals(messageContent)) {
                                    currentUser.editMessage(message, newContent);
                                    c++;
                                }
                            }
                            if (c == 0) {
                                out.write("1");
                                out.println();
                                out.flush();
                            } else {
                                out.write("2");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("7")) {
                        synchronized (gateKeeper) {
                            if (currentUser != null && currentUser.getRole().equals("Pharmacist")) {
                                users.remove(currentUser.getUsername());
                                currentUser = null;
                                out.write("1");
                                out.println();
                                out.flush();
                            } else {
                                out.write("2");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("8")) {
                        synchronized (gateKeeper) {
                            if (currentUser != null && currentUser.getRole().equals("Patient")) {
                                users.remove(currentUser.getUsername());
                                currentUser = null;
                                out.write("1");
                                out.println();
                                out.flush();
                            } else {
                                out.write("2");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("9")) {
                        synchronized (gateKeeper) {
                            String username = lines[1];
                            String email = lines[2];
                            String password = lines[3];
                            if (!username.isEmpty()) {
                                users.remove(currentUser.getUsername());
                                currentUser.setUsername(username);
                                users.put(currentUser.getUsername(), currentUser);
                            }
                            if (!email.isEmpty()) {
                                currentUser.setEmail(email);
                            }
                            if (!password.isEmpty()) {
                                currentUser.setPassword(password);
                            }
                        }
                    }

                    if (lines[0].equals("10")) {
                        synchronized (gateKeeper) {
                            String storeName = lines[1];
                            Pharmacy pharmacy = new Pharmacy(storeName, currentUser);
                            pharmacys.add(pharmacy);
                            currentUser.addPharmacys(pharmacy);
                        }
                    }

                    if (lines[0].equals("11")) {
                        synchronized (gateKeeper) {

                            String recipientName = lines[1];
                            String pathName = lines[2];
                            User recipient = users.get(recipientName);

                            if (recipient != null) {
                                String pathNameExtension = pathName.substring(pathName.indexOf('.'));
                                if (!pathNameExtension.equals(".csv")) {
                                    out.write("1");
                                    out.println();
                                    out.flush();
                                } else {
                                    File exportedMessages = new File(pathName);
                                    try {
                                        PrintWriter pw = new PrintWriter(new FileWriter(exportedMessages, true));
                                        List<Message> userMessages = currentUser.getMessages();
                                        for (Message message : userMessages) {
                                            pw.println(recipientName + " and " + currentUser.getUsername() + "," + message.getSenderName() + "," + message.getTimeStamp() + "," + message.getContent());
                                        }
                                        pw.close();
                                    } catch (IOException x) {
                                        out.write("2");
                                        out.println();
                                        out.flush();
                                    }
                                    out.write("3");
                                    out.println();
                                    out.flush();
                                }
                            } else {
                                out.write("4");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("12")) {
                        synchronized (gateKeeper) {
                            String patientList = "List of Patients:" + ",";
                            for (User user : users.values()) {
                                if (user.getRole().equals("Patient")) {
                                    patientList += user.getUsername() + ",";
                                }
                            }
                            out.write(patientList);
                            out.println();
                            out.flush();
                        }
                    }

                    if (lines[0].equals("13")) {
                        synchronized (gateKeeper) {
                            String storeNameString = lines[1];
                            String productNameString = lines[2];
                            String quantity = lines[3];
                            for (Pharmacy pharmacy : pharmacys) {
                                if (pharmacy.getName().equals(storeNameString)) {
                                    Product newProduct = new Product(productNameString, Integer.parseInt(quantity));
                                    pharmacy.addProduct(newProduct);
                                    out.write("1");
                                    out.println();
                                    out.flush();
                                } else {
                                    out.write("2");
                                    out.println();
                                    out.flush();
                                }
                            }
                        }
                    }

                    if (lines[0].equals("14")) {
                        synchronized (gateKeeper) {
                            String username = lines[1];
                            User recipient = users.get(username);
                            String content = lines[2];
                            if (currentUser != null) {
                                if (recipient.isUserBlocked(currentUser)) {
                                    out.write("1");
                                    out.println();
                                    out.flush();
                                } else {
                                    if (currentUser.getRole().equals("Pharmacist") && recipient.getRole().equals("Patient")) {
                                        Message message = new Message(currentUser, recipient, content);
                                        currentUser.receiveMessage(message);
                                        recipient.receiveMessage(message);
                                        out.write("2");
                                        out.println();
                                        out.flush();
                                    } else if (currentUser.getRole().equals("Patient") && recipient.getRole().equals("Pharmacist")) {
                                        Message message = new Message(currentUser, recipient, content);
                                        currentUser.receiveMessage(message);
                                        recipient.receiveMessage(message);
                                        out.write("3");
                                        out.println();
                                        out.flush();
                                    } else {
                                        out.write("4");
                                        out.println();
                                        out.flush();
                                    }
                                }
                            } else {
                                out.write("5");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("15")) {
                        synchronized (gateKeeper) {
                            String messageContent = lines[1];
                            int count = 0;
                            for (Message message : currentUser.getMessages()) {
                                if (message.getContent().equals(messageContent)) {
                                    currentUser.deleteMessage(message);
                                    count++;
                                    out.write("1");
                                    out.println();
                                    out.flush();
                                }
                            }
                            if (count == 0) {
                                out.write("2");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("16")) {
                        synchronized (gateKeeper) {
                            String recipientUsername = lines[1];
                            String messagePath = lines[2];

                            if (users.containsKey(recipientUsername)) {
                                User recipient = users.get(recipientUsername);
                                if (recipient.isUserBlocked(currentUser)) {
                                    out.write("1");
                                    out.println();
                                    out.flush();
                                } else if (currentUser.isUserBlocked(recipient)) {
                                    out.write("2");
                                    out.println();
                                    out.flush();
                                } else {
                                    StringBuilder str = new StringBuilder();
                                    if (recipient.getUsername().equals(currentUser.getUsername())) {
                                        out.write("3");
                                        out.println();
                                        out.flush();
                                    } else {
                                        try {
                                            BufferedReader bfr = new BufferedReader(new FileReader(messagePath));
                                            str.append(bfr.readLine());
                                            str.append("\n");

                                            String content = str.toString().trim();
                                            Message message = new Message(currentUser, recipient, content);
                                            currentUser.receiveMessage(message);
                                            recipient.receiveMessage(message);
                                            out.write("4");
                                            out.println();
                                            out.flush();
                                        } catch (IOException exception) {
                                            out.write("5");
                                            out.println();
                                            out.flush();
                                        }
                                    }
                                }
                            } else {
                                out.write("6");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("17")) {
                        synchronized (gateKeeper) {
                            String pharmacistUsername = lines[1];

                            if (currentUser != null && currentUser.getRole().equals("Patient")) {
                                User pharmacist = users.get(pharmacistUsername);
                                if (pharmacist != null) {
                                    if (pharmacist.isUserBlocked(currentUser)) {
                                        out.write("1");
                                        out.println();
                                        out.flush();
                                    } else {
                                        if (pharmacist.getRole().equals("Pharmacist")) {
                                            out.write("2");
                                            out.println();
                                            out.flush();
                                        }
                                    }
                                } else {
                                    out.write("3");
                                    out.println();
                                    out.flush();
                                }
                            } else {
                                out.write("4");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("18")) {
                        synchronized (gateKeeper) {
                            String patientUsername = lines[1];

                            if (currentUser != null && currentUser.getRole().equals("Pharmacist")) {
                                User patient = users.get(patientUsername);
                                if (patient != null) {
                                    if (patient.isUserBlocked(currentUser)) {
                                        out.write("1");
                                        out.println();
                                        out.flush();
                                    } else {
                                        if (patient.getRole().equals("Patient")) {
                                            out.write("2");
                                            out.println();
                                            out.flush();
                                        }
                                    }
                                } else {
                                    out.write("3");
                                    out.println();
                                    out.flush();
                                }
                            } else {
                                out.write("4");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("19")) {
                        synchronized (gateKeeper) {
                            String pharmacyList = "Available Pharmacies:" + ",";
                            for (Pharmacy pharmacy : pharmacys) {
                                pharmacyList += pharmacy.getName() + " (Pharmacist: " + pharmacy.getPharmacist().getUsername() + "),";
                            }
                            System.out.println(pharmacyList);
                            out.write(pharmacyList);
                            out.println();
                            out.flush();
                        }
                    }

                    if (lines[0].equals("20")) {
                        synchronized (gateKeeper) {
                            String pharmacyName = lines[1];
                            Pharmacy selectedPharmacy = null;
                            for (Pharmacy pharmacy : pharmacys) {
                                if (pharmacy.getName().equals(pharmacyName)) {
                                    selectedPharmacy = pharmacy;
                                }
                            }
                            if (selectedPharmacy != null) {
                                String recipientName = selectedPharmacy.getPharmacist().getUsername();
                                User recipient = users.get(recipientName);
                                if (recipient != null) {
                                    out.write("1" + recipientName);
                                    out.println();
                                    out.flush();
                                } else {
                                    out.write("2");
                                    out.println();
                                    out.flush();
                                }
                            } else {
                                out.write("3");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("21")) {
                        synchronized (gateKeeper) {
                            String pharmacyName = lines[1];
                            String pathName = lines[2];
                            Pharmacy selectedPharmacy = null;
                            for (Pharmacy pharmacy : pharmacys) {
                                if (pharmacy.getName().equals(pharmacyName)) {
                                    selectedPharmacy = pharmacy;
                                }
                            }
                            if (selectedPharmacy != null) {
                                try {
                                    BufferedReader bfr = new BufferedReader(new FileReader(pathName));
                                    StringBuilder str = new StringBuilder();
                                    String line;
                                    while ((line = bfr.readLine()) != null) {
                                        str.append(line).append("\n");
                                    }
                                    bfr.close();

                                    String fileContent = str.toString().trim();
                                    String recipientNameFromFile = selectedPharmacy.getPharmacist().getUsername();
                                    User recipientFromFile = users.get(recipientNameFromFile);
                                    if (recipientFromFile != null) {
                                        out.write("1" + recipientFromFile + ",," + fileContent);
                                        out.println();
                                        out.flush();
                                    } else {
                                        out.write("2");
                                        out.println();
                                        out.flush();
                                    }
                                } catch (IOException ex) {
                                    out.write("3");
                                    out.println();
                                    out.flush();
                                }
                            } else {
                                out.write("4");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("22")) {
                        synchronized (gateKeeper) {
                            String pharmacyName = lines[1];
                            String productName = lines[2];
                            String quantityStr = lines[3];
                            Pharmacy pharmacy = null;
                            for (Pharmacy pharmacies : pharmacys) {
                                if (pharmacies.getName().equals(pharmacyName)) {
                                    pharmacy = pharmacies;
                                }
                            }
                            if (pharmacy != null) {
                                Product product = pharmacy.findProductByName(productName);

                                if (product != null) {
                                    int quantityPurchased;

                                    try {
                                        quantityPurchased = Integer.parseInt(quantityStr);
                                    } catch (NumberFormatException ex) {
                                        out.write("5");
                                        out.println();
                                        out.flush();
                                        return;
                                    }

                                    int currentQuantity = product.getQuantity();

                                    if (currentQuantity >= quantityPurchased) {
                                        product.setQuantity(currentQuantity - quantityPurchased);
                                        out.write("1");
                                        out.println();
                                        out.flush();
                                    } else {
                                        out.write("2");
                                        out.println();
                                        out.flush();
                                    }
                                } else {
                                    out.write("3");
                                    out.println();
                                    out.flush();
                                }
                            } else {
                                out.write("4");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    if (lines[0].equals("23")) {
                        synchronized (gateKeeper) {
                            String patientUsername = lines[1];
                            User patient = users.get(patientUsername);
                            if (patient != null && patient.getRole().equals("Patient")) {
                                out.write("1");
                                out.println();
                                out.flush();
                            }
                        }
                    }

                    inputLine = in.readLine();
                }
                userOut(users);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
