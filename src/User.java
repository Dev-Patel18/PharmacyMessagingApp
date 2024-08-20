import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class User implements Serializable {
    // User properties
    private String username;
    private String email;
    public String password; // Note: Direct access to password is not recommended for security reasons
    private String role;

    // User associations
    private List<Message> messages = new ArrayList<>();
    private List<User> blockedUsers = new ArrayList<User>();
    private List<Pharmacy> pharmacys = new ArrayList<>();

    // Method to block a user
    public void blockUser(User userToBlock) {
        blockedUsers.add(userToBlock);
    }

    // Method to add a pharmacy to the user's list
    public void addPharmacys(Pharmacy pharmacy) {
        pharmacys.add(pharmacy);
    }

    // Method to get the list of pharmacys associated with the user
    public List<Pharmacy> getPharmacys() {
        return pharmacys;
    }

    // Method to check if a user is blocked
    public boolean isUserBlocked(User user) {
        return blockedUsers.contains(user);
    }

    // Constructor to initialize a user with basic information
    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;

    }



    // Default constructor
    public User() {
    }

    // Getter method for username
    public String getUsername() {
        return username;
    }

    // Getter method for email
    public String getEmail() {
        return email;
    }

    // Getter method for role
    public String getRole() {
        return role;
    }

    // Getter method for the list of messages associated with the user
    public List<Message> getMessages() {
        return messages;
    }

    // Method to send a message to a recipient user

    // Method to receive a message
    public void receiveMessage(Message message) {
        messages.add(message);
    }

    // Method to edit a message (if the user is the sender)
    public void editMessage(Message message, String newContent) {
        if (messages.contains(message) && this.equals(message.getSender())) {
            message.setContent(newContent);
        } else {
            System.out.println("You cannot edit this message.");
        }
    }

    // Method to delete a message (if the user is the sender)
    public void deleteMessage(Message message) {
        if (messages.contains(message) && this.equals(message.getSender())) {
            messages.remove(message);
        } else {
            System.out.println("You cannot delete this message.");
        }
    }

    // Method to remove a message
    public void removeMessage(Message message) {
        messages.remove(message);
    }

    // Method to set a new username
    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    // Method to set a new email
    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    // Method to set a new password
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}
