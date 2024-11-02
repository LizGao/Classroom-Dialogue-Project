import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    static class ClientHandler extends Thread {

        DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
        DateFormat fortime = new SimpleDateFormat("hh:mm:ss");

        private Socket clientSocket;
        private DataInputStream in;
        private DataOutputStream out;

        // Constructor
        public ClientHandler(Socket client, DataInputStream in, DataOutputStream out) {
            this.in = in;
            this.out = out;
            this.clientSocket = client;
        }

        public void sendMessage(String message) throws IOException {
            out.write(message.getBytes());
        }

        @Override
        public void run() {
            String received;
            String toreturn;
            while (true)
            {
                try {

                    // Ask user what he wants
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

                    // Write back
                    out.writeUTF("You said: " + received);
                    Date date = new Date();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try
            {
                // closing resources
                this.in.close();
                this.out.close();

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void handleHTTPRequest(Socket client, StringBuilder request) throws IOException {
        // Print request
        System.out.println("--REQUEST--");
        System.out.println(request);

        // Start a classroom
        String[] requestText = request.toString().split(" ");
        if (requestText[1].equals("/Classroom")) {
            // TODO Pull up the class user list
//            ClientHandler host = new ClientHandler(client);
//            hosts.add(host);
//            new Thread(host).start();
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
            try {
                host.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

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

                Thread clientHandler = new ClientHandler(clientSocket, in, out);
                hosts.add((ClientHandler) clientHandler);
                clientHandler.start();

            } catch (IOException e) {
                serverSocket.close();
                e.printStackTrace();
            }
        }

    }


    public static void main (String[] args) {

        Server myServer = new Server();
        try {
            myServer.startServer();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

}