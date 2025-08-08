import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClientGUI extends JFrame {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected = false;
    private String username;
    
    // GUI Components
    private JTextPane chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton connectButton;
    private JButton disconnectButton;
    private JLabel statusLabel;
    private JLabel usernameLabel;
    private JTextField usernameField;
    private StyledDocument chatDocument;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    // Styles for chat messages
    private Style userStyle;
    private Style otherStyle;
    private Style systemStyle;
    private Style timeStyle;
    
    public ChatClientGUI() {
        initializeComponents();
        setupLayout();
        applyModernStyling();
        setupStyles();
    }
    
    private void initializeComponents() {
        setTitle("💬 Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);
        
        // Chat components
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatDocument = chatArea.getStyledDocument();
        
        messageField = new JTextField();
        messageField.setEnabled(false);
        
        sendButton = new JButton("📤 Send");
        sendButton.setEnabled(false);
        
        // Connection components
        usernameField = new JTextField("Enter username...");
        connectButton = new JButton("🔗 Connect");
        disconnectButton = new JButton("❌ Disconnect");
        disconnectButton.setEnabled(false);
        
        statusLabel = new JLabel("Status: Disconnected");
        usernameLabel = new JLabel("Not connected");
        
        // Event listeners
        setupEventListeners();
    }
    
    private void setupEventListeners() {
        sendButton.addActionListener(e -> sendMessage());
        
        messageField.addActionListener(e -> sendMessage());
        
        connectButton.addActionListener(e -> connectToServer());
        
        disconnectButton.addActionListener(e -> disconnectFromServer());
        
        // Username field focus events
        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals("Enter username...")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText("Enter username...");
                    usernameField.setForeground(Color.GRAY);
                }
            }
        });
        
        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnectFromServer();
                System.exit(0);
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Chat area (center)
        JPanel chatPanel = createChatPanel();
        add(chatPanel, BorderLayout.CENTER);
        
        // Message input (bottom)
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerPanel.setBackground(new Color(52, 58, 64));
        
        // Title
        JLabel titleLabel = new JLabel("💬 Chat Application");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        // User info panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(52, 58, 64));
        
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        statusLabel.setForeground(new Color(220, 53, 69));
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        userPanel.add(usernameLabel);
        userPanel.add(Box.createHorizontalStrut(15));
        userPanel.add(statusLabel);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        
        return chatPanel;
    }
    
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(new EmptyBorder(15, 20, 20, 20));
        
        // Connection panel
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionPanel.add(new JLabel("Username:"));
        usernameField.setPreferredSize(new Dimension(150, 35));
        connectionPanel.add(usernameField);
        connectionPanel.add(Box.createHorizontalStrut(10));
        connectionPanel.add(connectButton);
        connectionPanel.add(disconnectButton);
        
        // Message panel
        JPanel messagePanel = new JPanel(new BorderLayout(10, 0));
        messageField.setPreferredSize(new Dimension(0, 40));
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        
        inputPanel.add(connectionPanel, BorderLayout.NORTH);
        inputPanel.add(messagePanel, BorderLayout.SOUTH);
        
        return inputPanel;
    }
    
    private void applyModernStyling() {
        // Set look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Use default
        }
        
        // Color scheme
        Color primaryColor = new Color(0, 123, 255);
        Color successColor = new Color(40, 167, 69);
        Color dangerColor = new Color(220, 53, 69);
        Color backgroundColor = new Color(248, 249, 250);
        
        // Apply background
        getContentPane().setBackground(backgroundColor);
        
        // Style chat area
        chatArea.setBackground(Color.WHITE);
        chatArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Style message field
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        // Style username field
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setForeground(Color.GRAY);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        
        // Style buttons
        styleButton(sendButton, primaryColor);
        styleButton(connectButton, successColor);
        styleButton(disconnectButton, dangerColor);
    }
    
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (button == sendButton) {
            button.setPreferredSize(new Dimension(100, 40));
        } else {
            button.setPreferredSize(new Dimension(120, 35));
        }
    }
    
    private void setupStyles() {
        // Create styles for different message types
        userStyle = chatDocument.addStyle("user", null);
        StyleConstants.setForeground(userStyle, new Color(0, 123, 255));
        StyleConstants.setBold(userStyle, true);
        
        otherStyle = chatDocument.addStyle("other", null);
        StyleConstants.setForeground(otherStyle, new Color(33, 37, 41));
        StyleConstants.setBold(otherStyle, true);
        
        systemStyle = chatDocument.addStyle("system", null);
        StyleConstants.setForeground(systemStyle, new Color(108, 117, 125));
        StyleConstants.setItalic(systemStyle, true);
        
        timeStyle = chatDocument.addStyle("time", null);
        StyleConstants.setForeground(timeStyle, new Color(108, 117, 125));
        StyleConstants.setFontSize(timeStyle, 12);
    }
    
    private void connectToServer() {
        String user = usernameField.getText().trim();
        if (user.isEmpty() || user.equals("Enter username...")) {
            JOptionPane.showMessageDialog(this, "Please enter a username!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            connected = true;
            username = user;
            
            // Update UI
            updateConnectionStatus(true);
            appendToChat("Connected to server!", systemStyle);
            
            // Start listening for messages
            new Thread(this::listenForMessages).start();
            
            // Send username to server
            out.println(username);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Could not connect to server!\nMake sure the server is running on " + 
                SERVER_HOST + ":" + SERVER_PORT, "Connection Error", JOptionPane.ERROR_MESSAGE);
            appendToChat("Failed to connect to server.", systemStyle);
        }
    }
    
    private void disconnectFromServer() {
        connected = false;
        
        try {
            if (out != null) {
                out.println("quit");
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // Ignore errors during disconnect
        }
        
        updateConnectionStatus(false);
        appendToChat("Disconnected from server.", systemStyle);
    }
    
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty() || !connected) {
            return;
        }
        
        if (message.equalsIgnoreCase("/users")) {
            out.println(message);
        } else {
            // Display own message
            appendUserMessage(username + ": " + message, true);
            out.println(message);
        }
        
        messageField.setText("");
        messageField.requestFocus();
    }
    
    private void listenForMessages() {
        try {
            String message;
            while (connected && (message = in.readLine()) != null) {
                final String finalMessage = message;
                SwingUtilities.invokeLater(() -> {
                    if (finalMessage.startsWith("Enter your username:") || 
                        finalMessage.startsWith("Welcome to the chat") ||
                        finalMessage.startsWith("Type 'quit'")) {
                        appendToChat(finalMessage, systemStyle);
                    } else if (finalMessage.startsWith("***")) {
                        appendToChat(finalMessage, systemStyle);
                    } else if (finalMessage.startsWith("===")) {
                        appendToChat(finalMessage, systemStyle);
                    } else if (finalMessage.startsWith("- ") || finalMessage.startsWith("=======")) {
                        appendToChat(finalMessage, systemStyle);
                    } else if (finalMessage.contains(": ")) {
                        appendUserMessage(finalMessage, false);
                    } else {
                        appendToChat(finalMessage, otherStyle);
                    }
                });
            }
        } catch (IOException e) {
            if (connected) {
                SwingUtilities.invokeLater(() -> {
                    appendToChat("Connection lost!", systemStyle);
                    updateConnectionStatus(false);
                });
            }
        }
    }
    
    private void appendUserMessage(String message, boolean isCurrentUser) {
        try {
            String timestamp = timeFormat.format(new Date());
            
            // Add timestamp
            chatDocument.insertString(chatDocument.getLength(), 
                "[" + timestamp + "] ", timeStyle);
            
            // Add message with appropriate style
            Style messageStyle = isCurrentUser ? userStyle : otherStyle;
            chatDocument.insertString(chatDocument.getLength(), 
                message + "\n", messageStyle);
            
            // Auto-scroll to bottom
            chatArea.setCaretPosition(chatDocument.getLength());
            
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private void appendToChat(String message, Style style) {
        try {
            chatDocument.insertString(chatDocument.getLength(), 
                message + "\n", style);
            chatArea.setCaretPosition(chatDocument.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    private void updateConnectionStatus(boolean isConnected) {
        this.connected = isConnected;
        
        SwingUtilities.invokeLater(() -> {
            if (isConnected) {
                statusLabel.setText("Status: Connected");
                statusLabel.setForeground(new Color(40, 167, 69));
                usernameLabel.setText("👤 " + username);
                
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);
                messageField.setEnabled(true);
                sendButton.setEnabled(true);
                usernameField.setEnabled(false);
                
                messageField.requestFocus();
            } else {
                statusLabel.setText("Status: Disconnected");
                statusLabel.setForeground(new Color(220, 53, 69));
                usernameLabel.setText("Not connected");
                
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                messageField.setEnabled(false);
                sendButton.setEnabled(false);
                usernameField.setEnabled(true);
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatClientGUI().setVisible(true);
        });
    }
}
