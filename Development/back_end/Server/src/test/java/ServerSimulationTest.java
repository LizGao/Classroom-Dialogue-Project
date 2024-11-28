import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ServerSimulationTest {

    private Server testServer;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        testServer = new Server("TestServer", "TestID");
        executorService = Executors.newSingleThreadExecutor();
    }

    @AfterEach
    void tearDown() {
        executorService.shutdownNow(); // Stop the server thread after tests
    }

    @Test
    void testConnectToServer() throws Exception {
        // Run the server in a separate thread
        executorService.submit(() -> {
            try {
                testServer.startServer();
            } catch (Exception e) {
                fail("Server encountered an exception: " + e.getMessage());
            }
        });

        // Allow the server to start
        Thread.sleep(1000);

        // Simulate a client connecting to the server
        Socket s = new Socket("localhost", 8080);

        // Assert the response from the server
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String responseLine = reader.readLine();
        assertNotNull(responseLine, "Server did not respond");
        assertTrue(responseLine.contains("What do you want to say?"), "Unexpected response: " + responseLine);

    }

    @Test
    void testSendMessageToServer() throws Exception {
        // Run the server in a separate thread
        executorService.submit(() -> {
            try {
                testServer.startServer();
            } catch (Exception e) {
                fail("Server encountered an exception: " + e.getMessage());
            }
        });

        // Allow the server to start
        Thread.sleep(1000);

        // Simulate a client connecting to the server
        try (Socket clientSocket = new Socket("localhost", 8080);
             DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
             DataInputStream in = new DataInputStream(clientSocket.getInputStream())) {

            // Send a mock message to the server
            String mockRequest = "Hi Server.";
            out.writeUTF(mockRequest); // Use writeUTF to send the request
            out.flush();

            // Read the response
            StringBuilder responseBuilder = new StringBuilder();
            while (true) {
                String response = in.readUTF(); // Use readUTF to match the server's writeUTF
                responseBuilder.append(response).append("\n");

                // Break the loop if the closing tag is found
                if (response.contains("</Message>")) {
                    break;
                }
            }

            // Assert the full response from the server
            String Response = responseBuilder.toString();
            assertNotNull(Response, "Server did not respond");
            assertTrue(Response.contains("Hi Server"), "Unexpected response: " + Response);
        }
    }
}
