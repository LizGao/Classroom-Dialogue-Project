import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 *
 *
 * A server function receiving HTTP
 *
 */


public class Server {

    // Fields
    public int stage;
    private static CopyOnWriteArrayList<ClassroomHost> hosts = new CopyOnWriteArrayList<>();

    // Constructor
    Server() {
        this.stage = 1;
    }

    // Classroom thread runnable
    static class ClassroomHost implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClassroomHost(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            //TODO
        }

    }

    public static void handleRequest(Socket client, StringBuilder request) throws IOException {
        // Print request
        System.out.println("--REQUEST--");
        System.out.println(request);


        // Start a classroom
        if (/* TODO */ !request.toString().equals("START_CLASSROOM")) {
            ClassroomHost host = new ClassroomHost(client);
            new Thread(host).start();
        }

        // Respond to request
        OutputStream clientOut = client.getOutputStream();
        clientOut.write("HTTP/1.1 200 OK\n".getBytes(StandardCharsets.UTF_8));
        clientOut.write("\n".getBytes(StandardCharsets.UTF_8));
        clientOut.write("Hi Nunu!\n".getBytes(StandardCharsets.UTF_8));
        clientOut.flush();


    }

    // Classroom thread
    public int hostClassroom(ScienceClass scienceClass) {
        Thread thread = new Thread(() -> {
            System.out.println("Starting Classroom");
        });
        thread.start();

        return 0;
    }


    // Start the server
    public void start() throws Exception {

        try (ServerSocket serverSocket = new ServerSocket(8000)) {

            System.out.println("Server started");       // Start Message
            while (true) {
                try (Socket client = serverSocket.accept()) {
                    System.out.println("Client connected: " + client.toString());       // Client connected

                    // Input handle
                    InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());

                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder request = new StringBuilder();

                    // Build a reqeust
                    String line = bufferedReader.readLine();
                    while (!(line.isBlank())) {
                        request.append(line + "\n");
                        line = bufferedReader.readLine();
                    }

                    handleRequest(client, request);

                    client.close();
                    System.out.println("Client socket closed.");

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