Chat Application Design Document

1. Project Overview
The Chat Application is a multi-user, text-based chat system implemented using Java sockets. It consists of:
•	ChatServer: Handles multiple client connections, broadcasts messages, and logs chat activity.
•	ChatClient: Connects to the server, allowing users to send and receive messages.
This project demonstrates core concepts of network programming, including socket communication, threading, and robust error handling.

2. System Design
Architecture:
•	The server listens for client connections on a specified port.
•	Each client connection is handled in a separate thread, allowing multiple users to chat simultaneously.
Key Components:
•	Sockets:
o	ServerSocket on the server for listening to client connections.
o	Socket on the client for connecting to the server.
•	Threads:
o	Each client is handled in a dedicated thread on the server to enable concurrent connections.

3. Implementation Details
Core Features:
1.	Multi-Client Support:
o	The server supports multiple clients using threads.
2.	Broadcasting:
o	Messages from one client are broadcast to all connected clients.
3.	Graceful Disconnection:
o	Clients can disconnect using the \q command.
o	Server handles unexpected disconnections.
4.	Username Support:
o	Clients specify a unique username when connecting.
o	Duplicate usernames are rejected.
5.	Chat Logging:
o	All chat messages and notifications are saved to chat_log.txt.
Key Design Decisions:
•	Threading:
o	Each client is handled in a separate thread for scalability.
•	Error Handling:
o	Robust handling of connection issues to ensure server stability.
•	Logging:
o	Chat activity is logged to provide a record of the session.

4. Usage Instructions
Compile the Code:
1.	Ensure both ChatServer.java and ChatClient.java are in the same package (chat).
2.	Compile using: javac ChatServer.java ChatClient.java
Run the Server:
1.	Start the server with a specific port: java ChatServer <port> 
Example: java ChatServer 12345
Run the Client:
1.	Start the client with the server's address and port: java ChatClient <server-address> <port>
Example: java ChatClient localhost 12345
Testing the Application:
•	Connect multiple clients.
•	Test features:
o	Send and receive messages.
o	Test username support (ensure duplicate names are rejected).
o	Graceful disconnection using \q.
o	Verify chat_log.txt for logged messages.

5. References
1.	Java Networking Documentation: https://docs.oracle.com/javase/tutorial/networking/
2.	Multi-Threading in Java: https://docs.oracle.com/javase/tutorial/essential/concurrency/
3.	File I/O in Java: https://docs.oracle.com/javase/tutorial/essential/io/

