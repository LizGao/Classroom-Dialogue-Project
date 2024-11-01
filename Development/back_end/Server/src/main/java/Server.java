import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;


/**
 *
 * A real-time server
 *
 */


public class Server {

    // Fields
    public int stage;
    private static CopyOnWriteArrayList<ClientHandler> hosts = new CopyOnWriteArrayList<>();

    // Constructor
    Server() {
        this.stage = 1;
    }

    // Classroom thread runnable
    static class ClientHandler implements Runnable {
        private Socket client;
        private PrintWriter out;
        private BufferedReader in;

        // Constructor
        public ClientHandler(Socket client) {
            this.client = client;
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void run() {
            try {
                // Set IO streams
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                this.sendMessage("Hi, Client.");
                String message;
                while ((message = in.readLine()) != null) {
                // TODO RESPOND ON EACH LINE OF INPUT
                    System.out.println("Server got client message: " + message);
                    Server.broadcast(message);                  // Broadcast to all clients
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Close everything
                try {
                    in.close();
                    out.close();
                    client.close();
                    hosts.remove(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void handleRequest(Socket client, StringBuilder request) throws IOException {
        // Print request
        System.out.println("--REQUEST--");
        System.out.println(request);

        // Start a classroom
        String[] requestText = request.toString().split(" ");
        if (requestText[1].equals("/Classroom")) {
            // TODO Pull up the class user list
            ClientHandler host = new ClientHandler(client);
            hosts.add(host);
            new Thread(host).start();
        } else {
            // Respond to request
            OutputStream clientOut = client.getOutputStream();
            clientOut.write("HTTP/1.1 200 OK\n".getBytes(StandardCharsets.UTF_8));
            clientOut.write("\n".getBytes(StandardCharsets.UTF_8));
            clientOut.write("Hi Nunu!\n".getBytes(StandardCharsets.UTF_8));
            clientOut.flush();
        }
    }

    // Broadcast received message to all clients
    public static void broadcast(String message) {
        for (ClientHandler host : hosts) {
            host.sendMessage(message);
        }
    }


    /**
     *
     * // Classroom thread entry
     * public int hostClassroom(ScienceClass scienceClass) {
     *         Thread thread = new Thread(() -> {
     *             System.out.println("Starting Classroom");
     *         });
     *         thread.start();
     *
     *         return 0;
     *     }
     *
     */


    // Start the server
    public void start() throws Exception {

        try (ServerSocket serverSocket = new ServerSocket(8080)) {

            System.out.println("Server started");       // Start Message
            while (true) {
                try (Socket client = serverSocket.accept()) {
                    System.out.println("Client connected: " + client.toString());       // Client connected

                    // Input organize
                    ClientHandler clientHandler = new ClientHandler(client);
                    hosts.add(clientHandler);
                    new Thread(clientHandler).start();

//                     client.close();
//                     System.out.println("Client socket closed.");

                }
            }
        }
    }


    public static void main (String[] args) {

        Server myServer = new Server();
        try {
            myServer.start();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

}