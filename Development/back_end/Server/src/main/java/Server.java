import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;


/**
 * Real-time responding server, all service should be called and delivered from this port.
 * Implements service server layer, handles the webSocket connections and handles clients in separate threads.
 * Managed and created by ServerKernel.
 */


public class Server {

    // Fields
    public String name;
    public int stage;
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    // Constructor
    Server(String name) {
        this.name = name;
        this.stage = 1;
    }


    /**
     * Receives HTTP requests and send back responds.
     * !! Use OUTSIDE of classroom
     * TODO Add a HTTPRequest class to break down requests
     * @param client
     * @param request
     * @throws IOException
     */
    public static void handleHTTPRequest(Socket client, StringBuilder request) throws IOException {
        // Print request
        System.out.println("--REQUEST--");
        System.out.println(request);

        // Start a classroom
        String[] requestText = request.toString().split(" ");
        if (requestText[1].equals("/Classroom")) {
            // ?Keep this?
        } else {
            // Respond to request
            OutputStream clientOut = client.getOutputStream();
            clientOut.write("HTTP/1.1 200 OK\n".getBytes(StandardCharsets.UTF_8));
            clientOut.write("\n".getBytes(StandardCharsets.UTF_8));
            clientOut.write("Hi Nunu!\n".getBytes(StandardCharsets.UTF_8));
            clientOut.flush();
        }
    }


// !!! Client methods:


    /**
     * Broadcasts received message to all connected clients.
     * @param message
     */
    public static void broadcast(String message) {
        for (ClientHandler client : clients) {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


// !!! Server methods:


    /**
     * Gets login request from the client and checks its password
     * @param clientSocket
     * @param in
     * @param out
     * @return clientHandler
     */
    public static ClientHandler login(Socket clientSocket, DataInputStream in, DataOutputStream out) {

        boolean correct = true;
        // TODO Implement this method
        ClientHandler clientHandler = null;
        User user = null;

        if (correct) {
            clientHandler = new ClientHandler(user, clientSocket, in, out);
        }

        return clientHandler;
    }



    /**
     * Entry method of Server execution flow.
     * Starts the server, called by main().
     * Forever waits to accept connections from clients, and create a thread for each client.
     * @throws Exception
     */
    public void startServer() throws Exception {

        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started");       // Start Message
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.toString());       // Client connected

                // Input organize
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                Thread clientHandler = login(clientSocket, in, out);
                //TODO handle null return value.
                clients.add((ClientHandler) clientHandler);
                clientHandler.start();

            } catch (IOException e) {
                serverSocket.close();           //! May cause server failure
                e.printStackTrace();
            }
        }

    }


// !!! ClientHandler class:
    /**
     * Client Handler, called by startServer().
     * Handles client connections under TCP connection (java Socket).
     * Current: longterm TCP connection, send back the same message as the client sends it.
     * ?Receive HTTP request, if request is to start or enter a classroom, keep the connection
     */
    static class ClientHandler extends Thread {

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

        @Override
        public void run() {
            String received;
            String toreturn;
            while (true) {
                try {

                    // Send message to Client, asking what they want.
                    out.writeUTF("What do you want to say?\n"+
                            "Type EXIT to terminate connection.");

                    // receive the answer from client
                    received = in.readUTF();

                    if(received.equals("EXIT"))
                    {
                        System.out.println("Client " + this.clientSocket + " sends exit...");
                        System.out.println("Closing this connection.");
                        this.clientSocket.close();
                        System.out.println("Connection closed");
                        break;
                    }

                    // Respond
                    out.writeUTF("You said: " + received);
                    Date date = new Date();

                } catch (SocketException e) {
                    System.out.println("Unexpected disconnection from client: " + this.clientSocket);
                    clients.remove(this);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try
            {
                // closing resources
                this.in.close();
                this.out.close();
                this.clientSocket.close();

            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main () {

        Server myServer = new Server("TestServer");
        try {
            myServer.startServer();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

}