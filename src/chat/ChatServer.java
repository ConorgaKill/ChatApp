package chat;

// Importing standard I/O classes for handling input and output streams
import java.io.*; // Provides classes like BufferedReader, PrintWriter, FileWriter, etc.

// Importing networking classes for creating server and client sockets
import java.net.*; // Provides classes like ServerSocket, Socket, InetAddress, etc.

// Importing utility classes for data structures and thread-safe operations
import java.util.*; // Provides classes like Set, HashSet, Collections, etc.

public class ChatServer {
    private static final String LOG_FILE = "chat_log.txt"; // Log file name
    private static final Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        int port = 12345; // Default port

        // Handle command-line argument for port
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port: " + port);
            }
        }

        // Initialise the log file
        try {
            initialiseLogFile();
        } catch (IOException e) {
            System.err.println("Failed to initialise log file: " + e.getMessage());
            return; // Exit if log file can't be created
        }

        System.out.println("ChatServer is starting...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);

                // Prompt for username and check conflicts
                if (!clientHandler.initialiseUsername()) {
                    clientSocket.close();
                    continue; // Reject client if username conflict
                }

                clientHandlers.add(clientHandler); // Add to the list of active clients
                clientHandler.start(); // Start the client thread
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    // Method to broadcast a message to all clients and log it
    private static void broadcastMessage(String message) {
        synchronized (clientHandlers) {
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.sendMessage(message);
            }
        }
        // Log the message
        try {
            logMessage(message);
        } catch (IOException e) {
            System.err.println("Failed to log message: " + e.getMessage());
        }
    }

    // Method to initialise the log file
    private static void initialiseLogFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, false))) {
            writer.println("=== Chat Log Started ===");
        }
    }

    // Method to append a message to the log file
    private static void logMessage(String message) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(message);
            writer.newLine();
        }
    }

    // Inner class to handle client communication
    private static class ClientHandler extends Thread {
        private final Socket socket;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);

                // Notify all clients about the new connection
                broadcastMessage(username + " has joined the chat.");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("\\q")) {
                        // Notify others about the disconnection and break the loop
                        broadcastMessage(username + " has left the chat.");
                        break;
                    }
                    System.out.println("Message from " + username + ": " + message);
                    broadcastMessage(username + ": " + message);
                }
            } catch (IOException e) {
                System.err.println("Connection lost with " + username + ": " + e.getMessage());
            } finally {
                // Notify other clients about the disconnection
                broadcastMessage(username + " has left the chat.");

                // Clean up on client disconnect
                System.out.println(username + " disconnected.");
                clientHandlers.remove(this);
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }

        // Initialise the username and check for conflicts
        public boolean initialiseUsername() throws IOException {
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("Enter your username:");
            username = in.readLine();

            if (username == null || username.trim().isEmpty()) {
                out.println("Invalid username. Connection rejected.");
                return false;
            }

            synchronized (clientHandlers) {
                for (ClientHandler clientHandler : clientHandlers) {
                    if (clientHandler.username.equalsIgnoreCase(username)) {
                        out.println("Username already in use. Connection rejected.");
                        return false;
                    }
                }
            }

            out.println("Welcome to the chat, " + username + "!");
            return true;
        }

        // Method to send a message to this client
        private void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }
    }
}
