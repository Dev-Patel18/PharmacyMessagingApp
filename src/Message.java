import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.Serializable;

public class Message implements Serializable {
    // Message properties
    private User sender;
    private User recipient;
    private String content;
    private String timeStamp;

    // Constructor to create a message with sender, recipient, and content
    public Message(User sender, User recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        // Set the timestamp to the current date and time
        this.timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    }

    // Getter method for message content
    public String getContent() {
        return content;
    }

    // Getter method for the sender's username
    public String getSenderName() {
        return sender.getUsername();
    }

    // Getter method for the sender user object
    public User getSender() {
        return sender;
    }

    // Getter method for the recipient user object
    public User getRecipient() {
        return recipient;
    }

    // Setter method to update the message content
    public void setContent(String content) {
        this.content = content;
    }

    // Getter method for the timestamp
    public String getTimeStamp() {
        return timeStamp;
    }
}
