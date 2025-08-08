import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class ChatServerGUI extends JFrame {
    private static final int PORT = 8080;
    private static Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private static ExecutorService executor = Executors.newCachedThreadPool();
    
    private JTextArea logArea;
    private JLabel statusLabel;
    private JLabel clientCountLabel;
    private JButton startButton;
    private JButton stopButton;
    private ServerSocket serverSocket;
    private boolean serverRunning = false;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    
    public ChatServerGUI() {
        initializeComponents();
        setupLayout();
        applyModernStyling();
    }
    
    private void initializeComponents() {
        setTitle("🖥️ Chat Server Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        
        statusLabel = new JLabel("Server Status: Stopped");
        clientCountLabel = new JLabel("Connected Clients: 0");
        
        startButton = new JButton("🚀 Start Server");
        stopButton = new JButton("⏹️ Stop Server");
        stopButton.setEnabled(false);
        
        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Chat Server Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(clientCountLabel);
        headerPanel.add(statusPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
        
        JLabel logLabel = new JLabel("📝 Server Logs");
        logLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        centerPanel.add(logLabel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(new EmptyBorder(15, 20, 20, 20));
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(stopButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
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
            // Use default look and feel if system one fails
        }
        
        // Color scheme
        Color successColor = new Color(92, 184, 92);
        Color dangerColor = new Color(217, 83, 79);
        Color backgroundColor = new Color(248, 249, 250);
        Color textColor = new Color(33, 37, 41);
        
        // Apply colors
        getContentPane().setBackground(backgroundColor);
        logArea.setBackground(Color.WHITE);
        logArea.setForeground(textColor);
        logArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Button styling
        styleButton(startButton, successColor);
        styleButton(stopButton, dangerColor);
        
        // Label styling
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        clientCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(textColor);
        clientCountLabel.setForeground(textColor);
    }
    
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            serverRunning = true;
            
            appendLog("🚀 Server started on port " + PORT);
            updateStatus("Running", new Color(92, 184, 92));
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            
            // Start accepting clients in background
            new Thread(this::acceptClients).start();
            
        } catch (IOException e) {
            appendLog("❌ Failed to start server: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to start server on port " + PORT, 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void stopServer() {
        serverRunning = false;
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            // Disconnect all clients
            for (ClientHandler client : new ArrayList<>(clients)) {
                client.disconnect();
            }
            
            executor.shutdown();
            
            appendLog("⏹️ Server stopped");
            updateStatus("Stopped", new Color(108, 117, 125));
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            updateClientCount();
            
        } catch (IOException e) {
            appendLog("❌ Error stopping server: " + e.getMessage());
        }
    }
    
    private void acceptClients() {
        while (serverRunning && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                String clientInfo = clientSocket.getInetAddress().getHostAddress();
                appendLog("👤 New client connected: " + clientInfo);
                
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                executor.submit(clientHandler);
                updateClientCount();
                
            } catch (IOException e) {
                if (serverRunning) {
                    appendLog("❌ Error accepting client: " + e.getMessage());
                }
            }
        }
    }
    
    public void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = timeFormat.format(new Date());
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    public void updateClientCount() {
        SwingUtilities.invokeLater(() -> {
            clientCountLabel.setText("Connected Clients: " + clients.size());
        });
    }
    
    private void updateStatus(String status, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Server Status: " + status);
            statusLabel.setForeground(color);
        });
    }
    
    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender && client.isConnected()) {
                client.sendMessage(message);
            }
        }
    }
    
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }
    
    public static Set<ClientHandler> getClients() {
        return clients;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatServerGUI().setVisible(true);
        });
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private boolean connected = true;
    private ChatServerGUI server;
    
    public ClientHandler(Socket socket, ChatServerGUI server) {
        this.socket = socket;
        this.server = server;
    }
    
    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            out.println("Enter your username:");
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "Anonymous_" + socket.getPort();
            }
            
            String joinMessage = "*** " + username + " joined the chat ***";
            server.appendLog("👤 " + username + " joined the chat");
            ChatServerGUI.broadcastMessage(joinMessage, this);
            
            out.println("Welcome to the chat, " + username + "!");
            out.println("Type 'quit' to exit, '/users' to see online users");
            
            String inputLine;
            while ((inputLine = in.readLine()) != null && connected) {
                if (inputLine.equalsIgnoreCase("quit")) {
                    break;
                } else if (inputLine.equalsIgnoreCase("/users")) {
                    sendOnlineUsers();
                } else if (!inputLine.trim().isEmpty()) {
                    String message = username + ": " + inputLine;
                    server.appendLog("💬 " + message);
                    ChatServerGUI.broadcastMessage(message, this);
                }
            }
            
        } catch (IOException e) {
            server.appendLog("❌ Error with client " + username + ": " + e.getMessage());
        } finally {
            disconnect();
        }
    }
    
    public void sendMessage(String message) {
        if (out != null && connected) {
            out.println(message);
        }
    }
    
    private void sendOnlineUsers() {
        StringBuilder userList = new StringBuilder("=== Online Users ===\n");
        for (ClientHandler client : ChatServerGUI.getClients()) {
            if (client.isConnected() && client.username != null) {
                userList.append("- ").append(client.username).append("\n");
            }
        }
        userList.append("===================");
        sendMessage(userList.toString());
    }
    
    public void disconnect() {
        connected = false;
        
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            server.appendLog("❌ Error closing socket: " + e.getMessage());
        }
        
        if (username != null) {
            String leaveMessage = "*** " + username + " left the chat ***";
            server.appendLog("👋 " + username + " left the chat");
            ChatServerGUI.broadcastMessage(leaveMessage, this);
        }
        
        ChatServerGUI.removeClient(this);
        server.updateClientCount();
    }
    
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
    
    public String getUsername() {
        return username;
    }
}
