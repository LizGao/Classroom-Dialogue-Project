import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

/**
 * Client Handler, called by Server.startServer().
 * Handles client connections under TCP connection (java Socket).
 * Current: longterm TCP connection, send back the same message as the client sends it.
 * ?Receive HTTP request, if request is to start or enter a classroom, keep the connection
 */


class ClientHandler {

    private User user;
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;

    // Constructor
    public ClientHandler(User user, Socket clientSocket, DataInputStream in, DataOutputStream out) {
        this.user = user;
        this.in = in;
        this.out = out;
        this.clientSocket = clientSocket;
    }

    public void sendMessage(String message) throws IOException {
        out.write(message.getBytes());
    }

    public void acceptClient() throws IOException {
        Runnable acceptClient = new Runnable() {
            @Override
            public void run() {
                String received;
                String toreturn;
                while (true) {
                    try {

                        // Send message to Client, asking what they want.
                        out.writeUTF("What do you want to say?\n" +
                                "Type EXIT to terminate connection.");

                        // receive the answer from client
                        received = in.readUTF();

                        if (received.equals("EXIT")) {
                            System.out.println("Client " + ClientHandler.this.clientSocket + " sends exit...");
                            System.out.println("Closing this connection.");
                            ClientHandler.this.clientSocket.close();
                            System.out.println("Connection closed");
                            break;
                        }

                        // Respond
                        out.writeUTF("You said: " + received);
                        Date date = new Date();

                    } catch (SocketException e) {
                        System.out.println("Unexpected disconnection from client: " + ClientHandler.this.clientSocket);
                        Server.clients.remove(this);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    // closing resources
                    ClientHandler.this.in.close();
                    ClientHandler.this.out.close();
                    ClientHandler.this.clientSocket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(acceptClient);
        thread.start();
    }

}