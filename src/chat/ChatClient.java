package chat;

import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java ChatClient <server-address> <port>");
            return;
        }

        String serverAddress = args[0];
        int serverPort;

        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number. Please use a valid integer.");
            return;
        }

        while (true) {
            try (Socket socket = new Socket(serverAddress, serverPort);
                 BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                 BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true)) {

                System.out.println("Connected to the server at " + serverAddress + ":" + serverPort);

                // Thread to read messages from the server
                Thread receiverThread = new Thread(() -> {
                    try {
                        String serverMessage;
                        while ((serverMessage = serverReader.readLine()) != null) {
                            System.out.println(serverMessage);
                        }
                    } catch (IOException e) {
                        System.err.println("Connection lost: " + e.getMessage());
                    }
                });
                receiverThread.start();

                // Main loop to send messages to the server
                String userInput;
                while ((userInput = consoleReader.readLine()) != null) {
                    if (userInput.equalsIgnoreCase("\\q")) {
                        System.out.println("Exiting chat...");
                        break;
                    }
                    serverWriter.println(userInput);
                }

                break; // Exit the loop if the client exits normally
            } catch (IOException e) {
                System.err.println("Unable to connect to the server. Retrying in 5 seconds...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
