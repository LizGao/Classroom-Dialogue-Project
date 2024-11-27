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

    // Server static info
    public static String name;
    public static int stage;
    public static String ID;
    public static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    // Server dynamic footprint, refresh at every heaerbeat.
    public static int numNewClients = 0;


    // Constructor
    Server(String name, String ID) {
        this.name = name;
        this.stage = 1;
        this.ID = ID;
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
            // Create clientHandler instance
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

                ClientHandler clientHandler = login(clientSocket, in, out);
                //TODO handle null return value.
                clients.add(clientHandler);
                numNewClients++;
                clientHandler.acceptClient();       // Start clientHandler

            } catch (IOException e) {
                serverSocket.close();           //! May cause server failure
                e.printStackTrace();
            }
        }
    }

    public void resetNumNewClients() {
        numNewClients = 0;
    }


// !!! MAIN()

    public static void main (String[] args) throws Exception {

        Server myServer = new Server("TestServer", "ID_randomeID");
        Heart heart =  new Heart(myServer);
        heart.startHeartBeat();
        try {
            myServer.startServer();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}