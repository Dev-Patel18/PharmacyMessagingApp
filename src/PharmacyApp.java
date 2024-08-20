import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.net.Socket;
import java.util.List;

public class PharmacyApp extends JComponent implements Runnable {

    private PharmacyApp app;

    private static JFrame frame;
    private static JPanel cardPanel;
    private static CardLayout cardLayout;
    private JTextArea messagesArea;
    public Socket socket1;



    public PharmacyApp() {
        setupNetworking();
    }


    public void sendMessage(String username, String content) {
        serverSend("14" + "," + username + "," + content);
        String sm = serverReceive();
        if (sm.equals("1")) {
            JOptionPane.showMessageDialog(null, "You have been blocked by this user.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (sm.equals("2")) {
            JOptionPane.showMessageDialog(null, "Message sent to the patient.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else if (sm.equals("3")) {
            JOptionPane.showMessageDialog(null, "Message sent to the pharmacist.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else if (sm.equals("4")) {
            JOptionPane.showMessageDialog(null, "You cannot message another user with the same role.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (sm.equals("5")) {
            JOptionPane.showMessageDialog(null, "You must be logged in to send messages.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void run() {
        frame = new JFrame("Pharmacy App");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        JPanel loginPanel = createLoginPanel();
        cardPanel.add(loginPanel, "Login");

        JPanel sellerPanel = sellerPanel();
        cardPanel.add(sellerPanel, "Seller");

        JPanel customerPanel = customerPanel();
        cardPanel.add(customerPanel, "Customer");

        frame.setContentPane(cardPanel);

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }

    public JPanel deleteAccountCustomerPanel() {
        JPanel deleteAccountCustomerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Delete Account");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        deleteAccountCustomerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);
        deleteAccountCustomerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(3, 2));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton deleteButton = new JButton("Delete Account");
        bottomPanel.add(deleteButton);

        // Action Listeners
        deleteButton.addActionListener(e -> {

                serverSend("8");
                String sm = serverReceive();
                if (sm.equals("1")) {
                    JOptionPane.showMessageDialog(deleteAccountCustomerPanel, "Account deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(cardPanel, "Login");
                } else if (sm.equals("2")) {
                    JOptionPane.showMessageDialog(deleteAccountCustomerPanel, "You must be logged in as a patient to delete your account.", "Error", JOptionPane.ERROR_MESSAGE);
                }

        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "EditCustomer");
            }
        });

        deleteAccountCustomerPanel.add(middlePanel, BorderLayout.CENTER);
        deleteAccountCustomerPanel.add(bottomPanel, BorderLayout.SOUTH);

        return deleteAccountCustomerPanel;
    }
    public JPanel editAccountCustomerPanel() {
        JPanel editAccountCustomerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Edit Account");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        editAccountCustomerPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);
        editAccountCustomerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(3, 2));

        JLabel nameLabel = new JLabel("Enter username");
        JTextField nameField = new JTextField();
        middlePanel.add(nameLabel);
        middlePanel.add(nameField);

        JLabel emailLabel = new JLabel("Edit Email:");
        JTextField emailField = new JTextField();
        middlePanel.add(emailLabel);
        middlePanel.add(emailField);

        JLabel passwordLabel = new JLabel("Edit Password:");
        JPasswordField passwordField = new JPasswordField();
        middlePanel.add(passwordLabel);
        middlePanel.add(passwordField);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton editButton = new JButton("Edit Account");
        bottomPanel.add(editButton);

        // Action Listeners

        editButton.addActionListener(e -> {
            String username = nameField.getText();
            username = username.trim();
            String email = emailField.getText();
            email = email.trim();
            String password = passwordField.getText();
            password = password.trim();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username field is empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (email.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Email field is empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Password field is empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                serverSend("9" + "," + username + "," + email + "," + password);
                JOptionPane.showMessageDialog(null, "Account edited", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(cardPanel, "Login");
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "EditCustomer");
            }
        });

        editAccountCustomerPanel.add(middlePanel, BorderLayout.CENTER);
        editAccountCustomerPanel.add(bottomPanel, BorderLayout.SOUTH);


        return editAccountCustomerPanel;
    }

    public JPanel blockUserCustomerPanel() {
        JPanel blockUserCustomerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Block Pharmacist");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        blockUserCustomerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        blockUserCustomerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(1, 2));

        JLabel usernameLabel = new JLabel("Enter username for who you would like to block");
        JTextField usernameField = new JTextField();
        middlePanel.add(usernameLabel);
        middlePanel.add(usernameField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton blockButton = new JButton("Block User");
        bottomPanel.add(blockButton);

        // Action Listeners
        blockButton.addActionListener(e -> {
            String blocked = usernameField.getText();
            blocked = blocked.trim();

            if (blocked.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username field is empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                serverSend("5" + "," + blocked);
                String sm = serverReceive();
                if (sm.equals("1")) {
                    JOptionPane.showMessageDialog(null, "Username not found", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (sm.equals("2")) {
                    JOptionPane.showMessageDialog(null, "User has been blocked", "Success!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "EditCustomer");
            }
        });



        blockUserCustomerPanel.add(middlePanel, BorderLayout.CENTER);
        blockUserCustomerPanel.add(bottomPanel, BorderLayout.SOUTH);



        return blockUserCustomerPanel;
    }
    public JPanel searchPharmacistPanel() {
        JPanel searchPharmacistPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Search Pharmacist");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        searchPharmacistPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        searchPharmacistPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(2, 2));

        JLabel usernameLabel = new JLabel("Enter pharmacist username to message");
        JTextField usernameField = new JTextField();
        middlePanel.add(usernameLabel);
        middlePanel.add(usernameField);

        JLabel messageLabel = new JLabel("Enter Message");
        JTextField messageField = new JTextField();
        middlePanel.add(messageLabel);
        middlePanel.add(messageField);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton createButton = new JButton("Search and send message");
        bottomPanel.add(createButton);

        // Action Listeners
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pharmacistUsername = usernameField.getText().trim();
                String content = messageField.getText();
                serverSend("17" + "," + pharmacistUsername);
                String sm = serverReceive();

                if (sm.equals("1")) {
                    JOptionPane.showMessageDialog(searchPharmacistPanel, "You cannot search this user because they have blocked you.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (sm.equals("2")) {
                    sendMessage(pharmacistUsername, content);
                } else if (sm.equals("3")) {
                    JOptionPane.showMessageDialog(searchPharmacistPanel, "Pharmacist not found.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (sm.equals("4")) {
                    JOptionPane.showMessageDialog(searchPharmacistPanel, "Only patients can search for and message pharmacists.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "viewMessagesCustomerPanel");
            }
        });

        searchPharmacistPanel.add(middlePanel, BorderLayout.CENTER);
        searchPharmacistPanel.add(bottomPanel, BorderLayout.SOUTH);

        return searchPharmacistPanel;
    }

    public JPanel viewPharmacistPanel() {
        JPanel viewPharmacistPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("View Pharmacys");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        viewPharmacistPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);
        viewPharmacistPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        JTextArea pharmacyListArea = new JTextArea(10, 20);
        pharmacyListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(pharmacyListArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "viewMessagesCustomerPanel");
            }
        });

        // Displaying pharmacist list and sending messages
        serverSend("19");
        String pharmacistList = serverReceive();
        pharmacistList = pharmacistList.replace(",", "\n");
        pharmacyListArea.setText(pharmacistList);

        JButton sendMessageButton = new JButton("Send Message");
        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pharmacyName = JOptionPane.showInputDialog("Enter the pharmacy name to message the pharmacist:");
                if (pharmacyName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Pharmacy field is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                    int messageInput = Integer.parseInt(JOptionPane.showInputDialog(
                            "Would you like to send a message or import a .txt file as a message?\nEnter 1 for a message and 2 for a .txt file"));


                    switch (messageInput) {
                        case 1:
                            serverSend("20" + "," + pharmacyName);
                            String sm1 = serverReceive();
                            if (sm1.charAt(0) == '1') {
                                String recipientName = sm1.substring(1);
                                String content = JOptionPane.showInputDialog("Enter message:");
                                sendMessage(recipientName, content);
                            } else if (sm1.equals("2")) {
                                JOptionPane.showMessageDialog(null, "Recipient not found.");
                            } else if (sm1.equals("3")) {
                                JOptionPane.showMessageDialog(null, "Pharmacy does not exist.");
                            }
                            break;

                        case 2:
                            String pathName = JOptionPane.showInputDialog("Please enter the .txt file's path you would like to import");
                            serverSend("21" + "," + pharmacyName + "," + pathName);
                            String sm2 = serverReceive();
                            if (sm2.charAt(0) == '1') {
                                String recipientNameFromFile = sm2.substring(1, sm2.indexOf(","));
                                String fileContent = sm2.substring(sm2.indexOf(",," + 1));
                                sendMessage(recipientNameFromFile, fileContent);
                            } else if (sm2.equals("2")) {
                                JOptionPane.showMessageDialog(null, "Recipient not found.");
                            } else if (sm2.equals("3")) {
                                JOptionPane.showMessageDialog(null, "File error");
                            } else if (sm2.equals("4")) {
                                JOptionPane.showMessageDialog(null, "Pharmacy does not exist.");
                            }
                            break;

                        default:
                            JOptionPane.showMessageDialog(null, "Invalid option, try again.");
                    }
            }
        });

        centerPanel.add(sendMessageButton, BorderLayout.SOUTH);
        viewPharmacistPanel.add(centerPanel, BorderLayout.CENTER);

        return viewPharmacistPanel;
    }

    public JPanel exportFileCustomerPanel() {
        JPanel exportFileCustomerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Export File");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        exportFileCustomerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        exportFileCustomerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(2, 2));

        JLabel exportName = new JLabel("Enter username of messages to export");
        JTextField exportField = new JTextField();
        middlePanel.add(exportName);
        middlePanel.add(exportField);

        JLabel fileName = new JLabel("enter file name ending in .csv to export");
        JTextField fileField = new JTextField();
        middlePanel.add(fileName);
        middlePanel.add(fileField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton createButton = new JButton("create csv file");
        bottomPanel.add(createButton);

        // Action Listeners
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String recipientName = exportField.getText();
                recipientName = recipientName.trim();
                String pathName = fileField.getText();
                pathName = pathName.trim();

                serverSend("11" + "," + recipientName + "," + pathName);

                if (recipientName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "This field is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (pathName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "This field is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String sm = serverReceive();
                    if (sm.equals("1")) {
                        JOptionPane.showMessageDialog(null, "File name does not end with .csv!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (sm.equals("2")) {
                        JOptionPane.showMessageDialog(null, "File error.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (sm.equals("3")) {
                        JOptionPane.showMessageDialog(null, "Messages exported", "Success!", JOptionPane.INFORMATION_MESSAGE);
                    } else if (sm.equals("4")) {
                        JOptionPane.showMessageDialog(null, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Seller");
            }
        });

        exportFileCustomerPanel.add(middlePanel, BorderLayout.CENTER);
        exportFileCustomerPanel.add(bottomPanel, BorderLayout.SOUTH);


        return exportFileCustomerPanel;
    }

    public JPanel editPanelCustomerPanel() {
        JPanel editPanelCustomerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Edit Messages");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        editPanelCustomerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        editPanelCustomerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(2, 2));

        JLabel contentLabel = new JLabel("Enter message content to edit");
        JTextField contentField = new JTextField();
        middlePanel.add(contentLabel);
        middlePanel.add(contentField);

        JLabel messageLabel = new JLabel("Enter new Message content");
        JTextField messageField = new JTextField();
        middlePanel.add(messageLabel);
        middlePanel.add(messageField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton createButton = new JButton("Edit Message");
        bottomPanel.add(createButton);

        // Action Listeners
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageContent = contentField.getText();
                messageContent = messageContent.trim();
                String newContent = messageField.getText();
                newContent = newContent.trim();

                if (messageContent.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "This field is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (newContent.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "This field is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    serverSend("6" + "," + messageContent + "," + newContent);
                    String sm = serverReceive();
                    if (sm.equals("2")) {
                        JOptionPane.showMessageDialog(null, "Message edited", "Success!", JOptionPane.INFORMATION_MESSAGE);
                    } else if (sm.equals("1")) {
                        JOptionPane.showMessageDialog(null, "Message not found", "Error", JOptionPane.ERROR_MESSAGE);
                    }                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Seller");
            }
        });



        editPanelCustomerPanel.add(middlePanel, BorderLayout.CENTER);
        editPanelCustomerPanel.add(bottomPanel, BorderLayout.SOUTH);


        return editPanelCustomerPanel;
    }
    public JPanel deleteMessageCustomerPanel() {
        JPanel deleteMessageCustomerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Delete Messages");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        deleteMessageCustomerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        deleteMessageCustomerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(1, 2));

        JLabel deleteLabel = new JLabel("Enter Message to delete");
        JTextField deleteField = new JTextField();
        middlePanel.add(deleteLabel);
        middlePanel.add(deleteField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton deleteButton = new JButton("Delete Message");
        bottomPanel.add(deleteButton);

        // Action Listeners
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageContent = deleteField.getText();
                messageContent = messageContent.trim();

                serverSend("15" + "," + messageContent);
                String sm = serverReceive();
                if (sm.equals("1")) {
                    JOptionPane.showMessageDialog(null, "Message deleted successfully", "Success!", JOptionPane.INFORMATION_MESSAGE);
                } else if (sm.equals("2")) {
                    JOptionPane.showMessageDialog(null, "Message could not be found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Seller");
            }
        });


        deleteMessageCustomerPanel.add(middlePanel, BorderLayout.CENTER);
        deleteMessageCustomerPanel.add(bottomPanel, BorderLayout.SOUTH);


        return deleteMessageCustomerPanel;
    }

    public static JPanel viewingMessagesCustomerPanel() {
        JPanel viewingMessagesCustomerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("View Messages");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        viewingMessagesCustomerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        viewingMessagesCustomerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Seller");
            }
        });

        return viewingMessagesCustomerPanel;
    }

    public JPanel inputFileCustomerPanel() {
        JPanel inputFileCustomerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Input File to Pharmacist");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputFileCustomerPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);

        inputFileCustomerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(2, 2));

        JLabel pathLabel = new JLabel("Input txt File Path");
        JTextField pathField = new JTextField();
        middlePanel.add(pathLabel);
        middlePanel.add(pathField);

        JLabel nameLabel = new JLabel("Enter username to send file to");
        JTextField nameField = new JTextField();
        middlePanel.add(nameLabel);
        middlePanel.add(nameField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton txtButton = new JButton("Send txt File");
        bottomPanel.add(txtButton);

        // Action Listeners
        txtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Seller");
            }
        });

        txtButton.addActionListener(e -> {
            String recipientUsername = nameField.getText();
            recipientUsername = recipientUsername.trim();
            String messagePath = pathField.getText();
            messagePath = messagePath.trim();
            serverSend("16" + ","  + recipientUsername + "," + messagePath);
            String sm = serverReceive();

            if (sm.equals("1")) {
                JOptionPane.showMessageDialog(null, "You have been blocked by this user.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("2")) {
                JOptionPane.showMessageDialog(null, "You have blocked this user.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("3")) {
                JOptionPane.showMessageDialog(null, "You cannot send a message to yourself", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("4")) {
                JOptionPane.showMessageDialog(null, "Message sent successfully", "Success!", JOptionPane.INFORMATION_MESSAGE);
            } else if (sm.equals("5")) {
                JOptionPane.showMessageDialog(null, "File does not exist. Ensure that your file path is correct.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("6")) {
                JOptionPane.showMessageDialog(null, "User does not exist", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        inputFileCustomerPanel.add(middlePanel, BorderLayout.CENTER);
        inputFileCustomerPanel.add(bottomPanel, BorderLayout.SOUTH);


        return inputFileCustomerPanel;
    }

    public JPanel sendMessageCustomerPanel() {
        JPanel sendMessageCustomerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Send Message to Pharmacist");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sendMessageCustomerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        sendMessageCustomerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(2, 2));

        JLabel usernameName = new JLabel("Enter recipient username");
        JTextField usernameField = new JTextField();
        middlePanel.add(usernameName);
        middlePanel.add(usernameField);

        JLabel messageName = new JLabel("Enter Message");
        JTextField messageField = new JTextField();
        middlePanel.add(messageName);
        middlePanel.add(messageField);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton messageButton = new JButton("Send Message");
        bottomPanel.add(messageButton);


        // Action Listeners
        messageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String recipient = usernameField.getText();
                recipient = recipient.trim();
                String content = messageField.getText();
                serverSend("3" + "," + recipient + "," + content);
                String sm = serverReceive();
                if (sm.equals("1")) {
                    JOptionPane.showMessageDialog(null, "You have blocked this user.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (sm.equals("2")) {
                    JOptionPane.showMessageDialog(null, "You have been blocked by this user.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (sm.equals("3")) {
                    JOptionPane.showMessageDialog(null, "Message sent successfully", "Success!", JOptionPane.INFORMATION_MESSAGE);
                } else if (sm.equals("4")) {
                    JOptionPane.showMessageDialog(null, "Message sent successfully", "Success!", JOptionPane.INFORMATION_MESSAGE);
                } else if (sm.equals("5")) {
                    JOptionPane.showMessageDialog(null, "You cannot message another user with the same role", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (sm.equals("6")) {
                    JOptionPane.showMessageDialog(null, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Seller");
            }
        });


        sendMessageCustomerPanel.add(middlePanel, BorderLayout.CENTER);
        sendMessageCustomerPanel.add(bottomPanel, BorderLayout.SOUTH);



        return sendMessageCustomerPanel;
    }

    public JPanel buyMedicinePanel() {
        JPanel buyMedicinePanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Buy Medicine");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        buyMedicinePanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);
        buyMedicinePanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(3, 2));

        JLabel nameLabel = new JLabel("Enter Pharmacy Name");
        JTextField nameField = new JTextField();
        middlePanel.add(nameLabel);
        middlePanel.add(nameField);

        JLabel productLabel = new JLabel("Product Name");
        JTextField productField = new JTextField();
        middlePanel.add(productLabel);
        middlePanel.add(productField);

        JLabel quantityLabel = new JLabel("Quantity");
        JTextField quantityField = new JTextField();
        middlePanel.add(quantityLabel);
        middlePanel.add(quantityField);

        buyMedicinePanel.add(middlePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton buyButton = new JButton("Buy");
        bottomPanel.add(buyButton);

        buyButton.addActionListener(e -> {
            String pharmacyName = nameField.getText().trim();
            String productName = productField.getText().trim();
            String quantityStr = quantityField.getText().trim();
            serverSend("22" + "," + pharmacyName + "," + productName + "," + quantityStr);
            String sm = serverReceive();

            if (sm.equals("1")) {
                JOptionPane.showMessageDialog(null, "Purchase successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else if (sm.equals("2")) {
                JOptionPane.showMessageDialog(null, "Not enough quantity available for purchase.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("3")) {
                JOptionPane.showMessageDialog(null, "Product not found in the pharmacy.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("4")) {
                JOptionPane.showMessageDialog(null, "Pharmacy not found.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("5")) {
                JOptionPane.showMessageDialog(null, "Invalid quantity. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Customer");
            }
        });

        buyMedicinePanel.add(bottomPanel, BorderLayout.SOUTH);

        return buyMedicinePanel;
    }

    public JPanel deleteAccountSellerPanel() {
        JPanel deleteAccountSellerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Delete Account");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        deleteAccountSellerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);
        deleteAccountSellerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(3, 2));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton deleteButton = new JButton("Delete Account");
        bottomPanel.add(deleteButton);

        // Action Listeners
        deleteButton.addActionListener(e -> {
            serverSend("7");
            String sm = serverReceive();
            if (sm.equals("1")) {
                JOptionPane.showMessageDialog(deleteAccountSellerPanel, "Account deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(cardPanel, "Login");
            } else if (sm.equals("2")) {
                JOptionPane.showMessageDialog(deleteAccountSellerPanel, "You must be logged in as a pharmacist to delete your account.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "EditSeller");
            }
        });

        deleteAccountSellerPanel.add(middlePanel, BorderLayout.CENTER);
        deleteAccountSellerPanel.add(bottomPanel, BorderLayout.SOUTH);

        return deleteAccountSellerPanel;
    }

    public JPanel editAccountSellerPanel() {
        JPanel editAccountSellerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Edit Account");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        editAccountSellerPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);
        editAccountSellerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(3, 2));

        JLabel nameLabel = new JLabel("Enter username");
        JTextField nameField = new JTextField();
        middlePanel.add(nameLabel);
        middlePanel.add(nameField);

        JLabel emailLabel = new JLabel("Edit Email:");
        JTextField emailField = new JTextField();
        middlePanel.add(emailLabel);
        middlePanel.add(emailField);

        JLabel passwordLabel = new JLabel("Edit Password:");
        JPasswordField passwordField = new JPasswordField();
        middlePanel.add(passwordLabel);
        middlePanel.add(passwordField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton editButton = new JButton("Edit Account");
        bottomPanel.add(editButton);

        // Action Listeners


        editButton.addActionListener(e -> {
            String username = nameField.getText();
            username = username.trim();
            String email = emailField.getText();
            email = email.trim();
            String password = passwordField.getText();
            password = password.trim();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username field is empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (email.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Email field is empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Password field is empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                serverSend("9" + "," + username + "," + email + "," + password);
                JOptionPane.showMessageDialog(null, "Account edited", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(cardPanel, "Login");
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "EditSeller");
            }
        });

        editAccountSellerPanel.add(middlePanel, BorderLayout.CENTER);
        editAccountSellerPanel.add(bottomPanel, BorderLayout.SOUTH);


        return editAccountSellerPanel;
    }

    public JPanel blockPatientPanel() {
        JPanel blockPatientPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Block Patient");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        blockPatientPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        blockPatientPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(1, 2));

        JLabel usernameLabel = new JLabel("Enter username for who you would like to block");
        JTextField usernameField = new JTextField();
        middlePanel.add(usernameLabel);
        middlePanel.add(usernameField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton blockButton = new JButton("Block User");
        bottomPanel.add(blockButton);

        // Action Listeners
        blockButton.addActionListener(e -> {
            String blocked = usernameField.getText();
            blocked = blocked.trim();
            if (blocked.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username field is empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                serverSend("5" + "," + blocked);
                String sm = serverReceive();
                if (sm.equals("1")) {
                    JOptionPane.showMessageDialog(null, "Username not found", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (sm.equals("2")) {
                    JOptionPane.showMessageDialog(null, "User has been blocked", "Success!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "EditSeller");
            }
        });

        blockPatientPanel.add(middlePanel, BorderLayout.CENTER);
        blockPatientPanel.add(bottomPanel, BorderLayout.SOUTH);



        return blockPatientPanel;
    }

    public JPanel viewPatientPanel() {
        JPanel viewPatientPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("View Patient");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        viewPatientPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);
        viewPatientPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        JTextArea patientListArea = new JTextArea(10, 20);
        patientListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(patientListArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "viewMessagesSellerPanel");
            }
        });

        // Displaying patient list and sending messages

        serverSend("12");
        String patientLists = serverReceive();
        patientLists = patientLists.replace("," , "\n");
        patientListArea.setText(patientLists);

        JButton sendMessageButton = new JButton("Send Message");
        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String patientUsername = JOptionPane.showInputDialog("Enter the patient username to message:");
                serverSend("23" + "," + patientUsername);
                String sm = serverReceive();

                if (sm.equals("1")) {
                    int messageInput = Integer.parseInt(JOptionPane.showInputDialog(
                            "Would you like to send a message or import a .txt file as a message?\nEnter 1 for a message and 2 for a .txt file"));

                    switch (messageInput) {
                        case 1:
                            String content = JOptionPane.showInputDialog("Enter message:");
                            sendMessage(patientUsername, content);
                            break;

                        case 2:
                            String pathName = JOptionPane.showInputDialog("Please enter the .txt file's path you would like to import");
                            try {
                                BufferedReader bfr = new BufferedReader(new FileReader(pathName));
                                StringBuilder str = new StringBuilder();
                                String line;
                                while ((line = bfr.readLine()) != null) {
                                    str.append(line).append("\n");
                                }
                                bfr.close();

                                String fileContent = str.toString().trim();
                                sendMessage(patientUsername, fileContent);
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(null, "File error: " + ex.getMessage());
                            }
                            break;

                        default:
                            JOptionPane.showMessageDialog(null, "Invalid option, try again.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Patient not found.");
                }
            }
        });

        centerPanel.add(sendMessageButton, BorderLayout.SOUTH);
        viewPatientPanel.add(centerPanel, BorderLayout.CENTER);

        return viewPatientPanel;
    }

    public JPanel searchPatientPanel() {
        JPanel searchPatientPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Search Patient");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        searchPatientPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        searchPatientPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(2, 2));

        JLabel usernameLabel = new JLabel("Enter patient username to message");
        JTextField usernameField = new JTextField();
        middlePanel.add(usernameLabel);
        middlePanel.add(usernameField);

        JLabel messageLabel = new JLabel("Enter Message");
        JTextField messageField = new JTextField();
        middlePanel.add(messageLabel);
        middlePanel.add(messageField);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton createButton = new JButton("Search and send message");
        bottomPanel.add(createButton);

        // Action Listeners
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String patientUsername = usernameField.getText().trim();
                String content = messageField.getText();

                serverSend("18" + "," + patientUsername);
                String sm = serverReceive();

                if (sm.equals("1")) {
                    JOptionPane.showMessageDialog(searchPatientPanel, "You cannot search this user because they have blocked you.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (sm.equals("2")) {
                    sendMessage(patientUsername, content);
                } else if (sm.equals("3")) {
                    JOptionPane.showMessageDialog(searchPatientPanel, "Patient not found.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (sm.equals("4")) {
                    JOptionPane.showMessageDialog(searchPatientPanel, "Only pharmacists can search for and message patients.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "viewMessagesSellerPanel");
            }
        });

        searchPatientPanel.add(middlePanel, BorderLayout.CENTER);
        searchPatientPanel.add(bottomPanel, BorderLayout.SOUTH);

        return searchPatientPanel;
    }

    public JPanel deleteMessageSellerPanel() {
        JPanel deleteMessageSellerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Delete Message");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        deleteMessageSellerPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);
        deleteMessageSellerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(1, 2));

        JLabel deleteLabel = new JLabel("Enter Message to delete");
        JTextField deleteField = new JTextField();
        middlePanel.add(deleteLabel);
        middlePanel.add(deleteField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton deleteButton = new JButton("Delete Message");
        bottomPanel.add(deleteButton);

        // Action Listeners
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageContent = deleteField.getText();
                messageContent = messageContent.trim();

                serverSend("15" + "," + messageContent);
                String sm = serverReceive();
                if (sm.equals("1")) {
                    JOptionPane.showMessageDialog(null, "Message deleted successfully", "Success!", JOptionPane.INFORMATION_MESSAGE);
                } else if (sm.equals("2")) {
                    JOptionPane.showMessageDialog(null, "Message could not be found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Customer");
            }
        });

        deleteMessageSellerPanel.add(middlePanel, BorderLayout.CENTER);
        deleteMessageSellerPanel.add(bottomPanel, BorderLayout.SOUTH);



        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Customer");
            }
        });

        return deleteMessageSellerPanel;
    }

    public JPanel editMessageSellerPanel() {
        JPanel editMessageSellerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Edit Message");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        editMessageSellerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        editMessageSellerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(2, 2));

        JLabel contentLabel = new JLabel("Enter message content to edit");
        JTextField contentField = new JTextField();
        middlePanel.add(contentLabel);
        middlePanel.add(contentField);

        JLabel messageLabel = new JLabel("Enter new Message content");
        JTextField messageField = new JTextField();
        middlePanel.add(messageLabel);
        middlePanel.add(messageField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton createButton = new JButton("Edit Message");
        bottomPanel.add(createButton);

        // Action Listeners
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageContent = contentField.getText();
                messageContent = messageContent.trim();
                String newContent = messageField.getText();
                newContent = newContent.trim();

                if (messageContent.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "This field is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (newContent.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "This field is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    serverSend("6" + "," + messageContent + "," + newContent);
                    String sm = serverReceive();
                    if (sm.equals("2")) {
                        JOptionPane.showMessageDialog(null, "Message edited", "Success!", JOptionPane.INFORMATION_MESSAGE);
                    } else if (sm.equals("1")) {
                        JOptionPane.showMessageDialog(null, "Message not found", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Customer");
            }
        });


        editMessageSellerPanel.add(middlePanel, BorderLayout.CENTER);
        editMessageSellerPanel.add(bottomPanel, BorderLayout.SOUTH);





        return editMessageSellerPanel;
    }

    public JPanel exportFileSellerPanel() {
        JPanel exportFileSellerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Export File");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        exportFileSellerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        exportFileSellerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(2, 2));

        JLabel exportName = new JLabel("Enter username of messages to export");
        JTextField exportField = new JTextField();
        middlePanel.add(exportName);
        middlePanel.add(exportField);

        JLabel fileName = new JLabel("enter file name ending in .csv to export");
        JTextField fileField = new JTextField();
        middlePanel.add(fileName);
        middlePanel.add(fileField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton createButton = new JButton("create csv file");
        bottomPanel.add(createButton);

        // Action Listeners
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String recipientName = exportField.getText();
                recipientName = recipientName.trim();
                String pathName = fileField.getText();
                pathName = pathName.trim();


                if (recipientName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "This field is empty.", "Error", JOptionPane.ERROR_MESSAGE);

                } else if (pathName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "This field is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                } serverSend("11" + "," + recipientName + "," + pathName);

                if (recipientName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "This field is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (pathName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "This field is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String sm = serverReceive();
                    if (sm.equals("1")) {
                        JOptionPane.showMessageDialog(null, "File name does not end with .csv!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (sm.equals("2")) {
                        JOptionPane.showMessageDialog(null, "File error.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (sm.equals("3")) {
                        JOptionPane.showMessageDialog(null, "Messages exported", "Success!", JOptionPane.INFORMATION_MESSAGE);
                    } else if (sm.equals("4")) {
                        JOptionPane.showMessageDialog(null, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Customer");
            }
        });

        exportFileSellerPanel.add(middlePanel, BorderLayout.CENTER);
        exportFileSellerPanel.add(bottomPanel, BorderLayout.SOUTH);



        return exportFileSellerPanel;
    }

    public static JPanel viewingMessagesSellerPanel() {
        JPanel viewingMessagesSellerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Viewing Messages");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        viewingMessagesSellerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        viewingMessagesSellerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Customer");
            }
        });

        return viewingMessagesSellerPanel;
    }

    public JPanel sendMessageSellerPanel() {
        JPanel sendMessageSellerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Send Message to Customer");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sendMessageSellerPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);
        sendMessageSellerPanel.add(topPanel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(2, 2));

        JLabel usernameName = new JLabel("Enter recipient username");
        JTextField usernameField = new JTextField();
        middlePanel.add(usernameName);
        middlePanel.add(usernameField);

        JLabel messageName = new JLabel("Enter Message");
        JTextField messageField = new JTextField();
        middlePanel.add(messageName);
        middlePanel.add(messageField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton messageButton = new JButton("Send Message");
        bottomPanel.add(messageButton);

        // Action Listeners
        messageButton.addActionListener(e -> {

            String recipient = usernameField.getText();
            recipient = recipient.trim();
            String content = messageField.getText();
            serverSend("3" + "," + recipient + "," + content);
            String sm = serverReceive();
            if (sm.equals("1")) {
                JOptionPane.showMessageDialog(null, "You have blocked this user.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("2")) {
                JOptionPane.showMessageDialog(null, "You have been blocked by this user.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("3")) {
                JOptionPane.showMessageDialog(null, "Message sent successfully", "Success!", JOptionPane.INFORMATION_MESSAGE);
            } else if (sm.equals("4")) {
                JOptionPane.showMessageDialog(null, "Message sent successfully", "Success!", JOptionPane.INFORMATION_MESSAGE);
            } else if (sm.equals("5")) {
                JOptionPane.showMessageDialog(null, "You cannot message another user with the same role", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("6")) {
                JOptionPane.showMessageDialog(null, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Customer");
            }
        });

        sendMessageSellerPanel.add(middlePanel, BorderLayout.CENTER);
        sendMessageSellerPanel.add(bottomPanel, BorderLayout.SOUTH);






        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Customer");
            }
        });

        return sendMessageSellerPanel;
    }

    public JPanel inputFileSellerPanel() {
        JPanel inputFileSellerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Input file to customer");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputFileSellerPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);
        inputFileSellerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(2, 2));

        JLabel pathLabel = new JLabel("Input txt File Path");
        JTextField pathField = new JTextField();
        middlePanel.add(pathLabel);
        middlePanel.add(pathField);

        JLabel nameLabel = new JLabel("Enter username to send file to");
        JTextField nameField = new JTextField();
        middlePanel.add(nameLabel);
        middlePanel.add(nameField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton txtButton = new JButton("Send txt File");
        bottomPanel.add(txtButton);

        // Action Listeners
        txtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Message Customer");
            }
        });

        txtButton.addActionListener(e -> {
            String recipientUsername = nameField.getText();
            recipientUsername = recipientUsername.trim();
            String messagePath = pathField.getText();
            messagePath = messagePath.trim();

            serverSend("16" + "," + recipientUsername + "," + messagePath);
            String sm = serverReceive();

            if (sm.equals("1")) {
                JOptionPane.showMessageDialog(null, "You have been blocked by this user.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("2")) {
                JOptionPane.showMessageDialog(null, "You have blocked this user.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("3")) {
                JOptionPane.showMessageDialog(null, "You cannot send a message to yourself", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("4")) {
                JOptionPane.showMessageDialog(null, "Message sent successfully", "Success!", JOptionPane.INFORMATION_MESSAGE);
            } else if (sm.equals("5")) {
                JOptionPane.showMessageDialog(null, "File does not exist. Ensure that your file path is correct.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (sm.equals("6")) {
                JOptionPane.showMessageDialog(null, "User does not exist", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputFileSellerPanel.add(middlePanel, BorderLayout.CENTER);
        inputFileSellerPanel.add(bottomPanel, BorderLayout.SOUTH);

        return inputFileSellerPanel;
    }



    public JPanel addProductPanel() {
        JPanel addProductPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Add Product");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        addProductPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);
        addProductPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(3, 2));

        JLabel storeName = new JLabel("Name of Pharmacy:");
        JTextField storeField = new JTextField();
        middlePanel.add(storeName);
        middlePanel.add(storeField);

        JLabel productName = new JLabel("New Product Name");
        JTextField productField = new JTextField();
        middlePanel.add(productName);
        middlePanel.add(productField);

        JLabel quantityLabel = new JLabel("Enter Quantity");
        JTextField quantityField = new JTextField();
        middlePanel.add(quantityLabel);
        middlePanel.add(quantityField);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton createButton = new JButton("Add Product");
        bottomPanel.add(createButton);


        // Action Listeners


        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Seller");
            }
        });

        addProductPanel.add(middlePanel, BorderLayout.CENTER);
        addProductPanel.add(bottomPanel, BorderLayout.SOUTH);

        createButton.addActionListener(e -> {
            String storeNameString = storeField.getText();
            storeNameString = storeNameString.trim();
            String productNameString = productField.getText();
            productNameString = productNameString.trim();
            int quantity = 0;
            try {
                quantity = Integer.parseInt(quantityField.getText());
                if (storeNameString.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Pharmacy name field is empty", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (productNameString.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Product name field is empty", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    serverSend("13" + "," + storeNameString + "," + productNameString + "," + quantity);
                    String sm = serverReceive();
                    if (sm.equals("1")) {
                        JOptionPane.showMessageDialog(null, "Product created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        cardLayout.show(cardPanel, "Seller");
                    } else if (sm.equals("2")) {
                        JOptionPane.showMessageDialog(null, "Pharmacy does not exist", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null, "Quantity field is not an integer", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        return addProductPanel;
    }

    public JPanel createStorePanel() {
        JPanel createStorePanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Create Pharmacy");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        createStorePanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);
        createStorePanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(1, 2));



        JLabel storeLabel = new JLabel("Enter the Store Name");
        JTextField storeField = new JTextField();
        middlePanel.add(storeLabel);
        middlePanel.add(storeField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton createStoreButton = new JButton("Create Store");
        bottomPanel.add(createStoreButton);

        // Action Listeners
        createStoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Seller");
            }
        });

        createStorePanel.add(middlePanel, BorderLayout.CENTER);
        createStorePanel.add(bottomPanel, BorderLayout.SOUTH);

        createStoreButton.addActionListener(e -> {
            String storeName = storeField.getText();
            storeName = storeName.trim();
            if (storeName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Pharmacy name field is empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                serverSend("10" + "," + storeName);
                JOptionPane.showMessageDialog(null, "Store created!", "Success!", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(cardPanel, "Seller");
            }
        });

        return createStorePanel;
    }

    public JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("You must create an account or log in to access the application.");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(150, 20));
        middlePanel.add(usernameLabel);
        middlePanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(150, 20));
        middlePanel.add(passwordLabel);
        middlePanel.add(passwordField);

        JButton createAccountButton = new JButton("Create Account");
        JButton loginButton = new JButton("Login");
        createAccountButton.setPreferredSize(new Dimension(150, 30));
        loginButton.setPreferredSize(new Dimension(150, 30));
        middlePanel.add(createAccountButton);
        middlePanel.add(loginButton);




        JButton sellerButton = new JButton("Seller");
        JButton customerButton = new JButton("Customer");

        sellerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Seller");
            }
        });

        customerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Customer");
            }
        });



        loginPanel.add(middlePanel, BorderLayout.CENTER);


        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel createAccountPanel = createAccountPanel();
                cardPanel.add(createAccountPanel, "Create Account");
                cardLayout.show(cardPanel, "Create Account");
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                username = username.trim();
                String password = passwordField.getText();
                password = password.trim();

                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Username field is empty", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Password field is empty", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    serverSend("1" + "," + username + "," + password);
                    String data = serverReceive();
                    if (data.equals("1")) {
                        JOptionPane.showMessageDialog(null, "Username not found", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (data.equals("2")) {
                        JOptionPane.showMessageDialog(null, "Incorrect Password", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (data.equals("3")) {
                        cardLayout.show(cardPanel, "Customer");
                    } else if (data.equals("4")) {
                        cardLayout.show(cardPanel, "Seller");
                    }
                }


            }

        });

        return loginPanel;
    }

    public JPanel createAccountPanel() {
        JPanel createAccountPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        createAccountPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);
        createAccountPanel.add(topPanel, BorderLayout.NORTH);



        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(4, 2));

        JLabel nameLabel = new JLabel("Username:");
        JTextField nameField = new JTextField();
        middlePanel.add(nameLabel);
        middlePanel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        middlePanel.add(emailLabel);
        middlePanel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        middlePanel.add(passwordLabel);
        middlePanel.add(passwordField);

        JLabel roleLabel = new JLabel("Select Role (Pharmacist or Patient)");
        JTextField roleField = new JTextField();
        middlePanel.add(roleLabel);
        middlePanel.add(roleField);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));


        JButton createButton = new JButton("Create");
        bottomPanel.add(createButton);


        // Action Listeners
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Login");
            }
        });

        createButton.addActionListener(e -> {
            String username = nameField.getText();
            username = username.trim();
            String email = emailField.getText();
            email = email.trim();
            String password = passwordField.getText();
            password = password.trim();
            String role = roleField.getText();
            role = role.trim();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username field is empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (email.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Email field is empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Password field is empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!(role.equals("Pharmacist") || role.equals("Patient"))) {
                JOptionPane.showMessageDialog(null, "Your role must be either Pharmacist or Patient", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                serverSend("2" + "," + username + "," + email + "," + password + "," + role);
                String data = serverReceive();
                if (data.equals("1")) {
                    JOptionPane.showMessageDialog(null, "Username has already been taken", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (data.equals("2")) {
                    JOptionPane.showMessageDialog(null, "Email has already been taken", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (data.equals("3")) {
                    JOptionPane.showMessageDialog(null, "Account created!", "Success!", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(cardPanel, "Login");
                }
            }




        });



        createAccountPanel.add(middlePanel, BorderLayout.CENTER);
        createAccountPanel.add(bottomPanel, BorderLayout.SOUTH);

        return createAccountPanel;
    }
    //-----------------------------------------------------------------------------------------------------
    public JPanel viewMessagesSellerPanel() {
        JPanel viewPanel = new JPanel(new BorderLayout());


            JLabel titleLabel = new JLabel("View Messages");
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            viewPanel.add(titleLabel, BorderLayout.NORTH);
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton backButton = new JButton("Back");

            topPanel.add(backButton);

            viewPanel.add(topPanel, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            centerPanel.add(messagesArea);

            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            JButton searchPatient = new JButton("Search Patient");
            JButton viewPatient = new JButton("View patients and send message");

            bottomPanel.add(searchPatient);
            bottomPanel.add(viewPatient);

            // Action Listeners
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(cardPanel, "Seller");
                }
            });

            searchPatient.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JPanel searchPatient = searchPatientPanel();
                    cardPanel.add(searchPatient, "Search Patient Seller");
                    cardLayout.show(cardPanel, "Search Patient Seller");
                }
            });

            viewPatient.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JPanel viewPatient = viewPatientPanel();
                    cardPanel.add(viewPatient, "View Patient Seller");
                    cardLayout.show(cardPanel, "View Patient Seller");

                }
            });


            viewPanel.add(centerPanel, BorderLayout.CENTER);
            viewPanel.add(bottomPanel, BorderLayout.SOUTH);
        return viewPanel;
    }

    public JPanel viewMessagesCustomerPanel() {
        JPanel viewPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("View Messages");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        viewPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        viewPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        centerPanel.add(messagesArea);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton searchPharmacist = new JButton("Search Pharmacist");
        JButton viewPharmacist = new JButton("View Pharmacys and send message");

        bottomPanel.add(searchPharmacist);
        bottomPanel.add(viewPharmacist);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Customer");
            }
        });

        searchPharmacist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel searchPharmacist = searchPharmacistPanel();
                cardPanel.add(searchPharmacist, "Search Pharmacist Customer");
                cardLayout.show(cardPanel, "Search Pharmacist Customer");
            }
        });

        viewPharmacist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel viewPharmacist = viewPharmacistPanel();
                cardPanel.add(viewPharmacist, "View Pharmacist Customer");
                cardLayout.show(cardPanel, "View Pharmacist Customer");

            }
        });


        viewPanel.add(centerPanel, BorderLayout.CENTER);
        viewPanel.add(bottomPanel, BorderLayout.SOUTH);

        return viewPanel;
    }

    public JPanel createMessagingPanelSeller() {
        JPanel messagingPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Messaging");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messagingPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        messagingPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton sendMessage = new JButton("Send Message");
        JButton inputFile = new JButton("Input File");
        JButton exportFile = new JButton("Export File");
        JButton editMessage = new JButton("Edit Message");
        JButton deleteMessage = new JButton("Delete Message");




        centerPanel.add(sendMessage);
        centerPanel.add(inputFile);
        centerPanel.add(exportFile);
        centerPanel.add(editMessage);
        centerPanel.add(deleteMessage);




        // Action Listeners
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Seller");
            }
        });

        sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel sendMessageSeller = sendMessageSellerPanel();
                cardPanel.add(sendMessageSeller, "Send Message Seller");
                cardLayout.show(cardPanel, "Send Message Seller");
            }
        });

        inputFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel inputFileSeller = inputFileSellerPanel();
                cardPanel.add(inputFileSeller, "Input File Seller");
                cardLayout.show(cardPanel, "Input File Seller");

            }
        });


        exportFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel exportFileSeller = exportFileSellerPanel();
                cardPanel.add(exportFileSeller, "Export File Seller");
                cardLayout.show(cardPanel, "Export File Seller");
            }
        });

        editMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel editMessageSeller = editMessageSellerPanel();
                cardPanel.add(editMessageSeller, "Edit Message Seller");
                cardLayout.show(cardPanel, "Edit Message Seller");

            }
        });

        deleteMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel deleteMessageSeller = deleteMessageSellerPanel();
                cardPanel.add(deleteMessageSeller, "Delete Message Seller");
                cardLayout.show(cardPanel, "Delete Message Seller");

            }
        });

        messagingPanel.add(centerPanel, BorderLayout.CENTER);

        return messagingPanel;
    }

    public JPanel createMessagingPanelCustomer() {
        JPanel messagingPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Messaging");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messagingPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        messagingPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton sendMessage = new JButton("Send Message");
        JButton inputFile = new JButton("Input File");
        JButton exportFile = new JButton("Export File");
        JButton editMessage = new JButton("Edit Message");
        JButton deleteMessage = new JButton("Delete Message");

        centerPanel.add(sendMessage);
        centerPanel.add(inputFile);
        centerPanel.add(exportFile);
        centerPanel.add(editMessage);
        centerPanel.add(deleteMessage);

        // Action Listeners
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Customer");
            }
        });

        sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel sendMessageCustomer = sendMessageCustomerPanel();
                cardPanel.add(sendMessageCustomer, "Send Message Customer");
                cardLayout.show(cardPanel, "Send Message Customer");
            }
        });

        inputFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel inputFileCustomer = inputFileCustomerPanel();
                cardPanel.add(inputFileCustomer, "Input File Customer");
                cardLayout.show(cardPanel, "Input File Customer");

            }
        });


        exportFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel exportFileCustomer = exportFileCustomerPanel();
                cardPanel.add(exportFileCustomer, "Export File Customer");
                cardLayout.show(cardPanel, "Export File Customer");
            }
        });

        editMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel editMessageCustomer = editPanelCustomerPanel();
                cardPanel.add(editMessageCustomer, "Edit Message Customer");
                cardLayout.show(cardPanel, "Edit Message Customer");
            }
        });

        deleteMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel deleteMessageCustomer = deleteMessageCustomerPanel();
                cardPanel.add(deleteMessageCustomer, "Delete Message Customer");
                cardLayout.show(cardPanel, "Delete Message Customer");
            }
        });

        messagingPanel.add(centerPanel, BorderLayout.CENTER);

        return messagingPanel;
    }

    public JPanel sellerPanel() {
        JPanel sellerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Home-Screen");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sellerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Edit Account");
        JButton logoutButton = new JButton("Logout");
        JButton exitButton = new JButton("Exit");

        topPanel.add(editButton);
        topPanel.add(logoutButton);
        topPanel.add(exitButton);

        sellerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton createStoreButton = new JButton("Create Pharmacy");
        JButton addProductButton = new JButton("Add Product");
        JButton messageButton = new JButton("Message Customer");
        JButton viewMessages = new JButton("View Messages and message");



        centerPanel.add(createStoreButton);
        centerPanel.add(addProductButton);
        centerPanel.add(messageButton);
        centerPanel.add(viewMessages);



        // Action Listeners
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Login");
            }
        });

        messageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel messagingPanelSeller = createMessagingPanelSeller();
                cardPanel.add(messagingPanelSeller, "Message Customer");
                cardLayout.show(cardPanel, "Message Customer");
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel editPanelSeller = createEditPanelSeller();
                cardPanel.add(editPanelSeller, "EditSeller");
                cardLayout.show(cardPanel, "EditSeller");
            }
        });

        viewMessages.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverSend("4");
                String messages = serverReceive();
                messages = messages.replace(",", "\n");
                messagesArea = new JTextArea(messages);
                JPanel viewMessagesSeller = viewMessagesSellerPanel();
                cardPanel.add(viewMessagesSeller, "viewMessagesSellerPanel");
                cardLayout.show(cardPanel, "viewMessagesSellerPanel");
            }
        });


        createStoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel createStore = createStorePanel();
                cardPanel.add(createStore, "Create Store");
                cardLayout.show(cardPanel, "Create Store");
            }
        });

        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel addProduct = addProductPanel();
                cardPanel.add(addProduct, "Add Product");
                cardLayout.show(cardPanel, "Add Product");
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });


        sellerPanel.add(centerPanel, BorderLayout.CENTER);

        return sellerPanel;
    }

    public JPanel customerPanel() {
        JPanel customerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Home-Screen");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        customerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Edit Account");
        JButton logoutButton = new JButton("Logout");
        JButton exitButton = new JButton("Exit");

        //  Action Listeners
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Login");
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        topPanel.add(editButton);
        topPanel.add(logoutButton);
        topPanel.add(exitButton);

        customerPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton buyMedicineButton = new JButton("Buy Medicine");
        JButton messageButton = new JButton("Message Seller");
        JButton viewMessages = new JButton("View Messages and message");

        centerPanel.add(buyMedicineButton);
        centerPanel.add(messageButton);
        centerPanel.add(viewMessages);


        //Action Listeners
        viewMessages.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverSend("4");
                String messages = serverReceive();
                messages = messages.replace(",", "\n");
                messagesArea = new JTextArea(messages);
                JPanel viewMessagesSeller = viewMessagesCustomerPanel();
                cardPanel.add(viewMessagesSeller, "viewMessagesCustomerPanel");
                cardLayout.show(cardPanel, "viewMessagesCustomerPanel");
            }
        });

        messageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel messagingPanelCustomer = createMessagingPanelCustomer();
                cardPanel.add(messagingPanelCustomer, "Message Seller");
                cardLayout.show(cardPanel, "Message Seller");

            }
        });

        buyMedicineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel buyMedicine = buyMedicinePanel();
                cardPanel.add(buyMedicine, "Buy Medicine Customer");
                cardLayout.show(cardPanel, "Buy Medicine Customer");
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel editPanelCustomer = createEditPanelCustomer();
                cardPanel.add(editPanelCustomer, "EditCustomer");
                cardLayout.show(cardPanel, "EditCustomer");
            }
        });


        customerPanel.add(centerPanel, BorderLayout.CENTER);

        return customerPanel;
    }

    public JPanel createEditPanelSeller() {
        JPanel editPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Edit Account");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        editPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        editPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton blockUser = new JButton("Block User");
        JButton editAccount = new JButton("Edit Account");
        JButton deleteAccount = new JButton("Delete Account");




        centerPanel.add(blockUser);
        centerPanel.add(editAccount);
        centerPanel.add(deleteAccount);




        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Seller");
            }
        });

        editAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel editAccountSeller = editAccountSellerPanel();
                cardPanel.add(editAccountSeller, "Edit Account Seller");
                cardLayout.show(cardPanel, "Edit Account Seller");

            }
        });

        deleteAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel deleteAccountSeller = deleteAccountSellerPanel();
                cardPanel.add(deleteAccountSeller, "Delete Account Seller");
                cardLayout.show(cardPanel, "Delete Account Seller");

            }
        });

        blockUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel blockPatient = blockPatientPanel();
                cardPanel.add(blockPatient, "Block User Seller");
                cardLayout.show(cardPanel, "Block User Seller");

            }
        });


        editPanel.add(centerPanel, BorderLayout.CENTER);

        return editPanel;
    }


    public JPanel createEditPanelCustomer() {
        JPanel editPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Edit Account");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        editPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");

        topPanel.add(backButton);

        editPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton blockUser = new JButton("Block User");
        JButton editAccount = new JButton("Edit Account");
        JButton deleteAccount = new JButton("Delete Account");




        centerPanel.add(blockUser);
        centerPanel.add(editAccount);
        centerPanel.add(deleteAccount);



        // Action Listeners
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Customer");
            }
        });

        editAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel editAccountCustomer = editAccountCustomerPanel();
                cardPanel.add(editAccountCustomer, "Edit Account Customer");
                cardLayout.show(cardPanel, "Edit Account Customer");
            }
        });

        deleteAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel deleteAccountCustomer = deleteAccountCustomerPanel();
                cardPanel.add(deleteAccountCustomer, "Delete Account Customer");
                cardLayout.show(cardPanel, "Delete Account Customer");
            }
        });

        blockUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel blockUserCustomer = blockUserCustomerPanel();
                cardPanel.add(blockUserCustomer, "Block User Customer");
                cardLayout.show(cardPanel, "Block User Customer");
            }
        });


        editPanel.add(centerPanel, BorderLayout.CENTER);

        return editPanel;
    }

    public static void main(String[] args) {
        PharmacyApp appInstance = new PharmacyApp();
        SwingUtilities.invokeLater(new PharmacyApp());
    }

    private void serverSend(String message) {
        try {
            PrintWriter pw = new PrintWriter(socket1.getOutputStream());
            pw.write(message);
            pw.println();
            pw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String serverReceive() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setupNetworking() {
        try {
            socket1 = new Socket("localhost", 12345);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
