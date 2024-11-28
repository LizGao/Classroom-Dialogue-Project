import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @Test
    void testHandleHTTPRequest() throws Exception {
        // Simulate a Socket，use ByteArrayOutputStream to catch output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Socket mockSocket = new Socket() {
            @Override
            public OutputStream getOutputStream() {
                return outputStream;
            }
        };

        StringBuilder request = new StringBuilder("GET / HTTP/1.1");
        Server.handleHTTPRequest(mockSocket, request);

        String response = outputStream.toString();
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Hi Nunu!"));
    }

    @Test
    void testBroadcast() throws Exception {
        // Creat two ClientHandlers
        ByteArrayOutputStream client1Out = new ByteArrayOutputStream();
        ByteArrayOutputStream client2Out = new ByteArrayOutputStream();

        ClientHandler client1 = new ClientHandler(null, null, null, new DataOutputStream(client1Out));
        ClientHandler client2 = new ClientHandler(null, null, null, new DataOutputStream(client2Out));

        Server.clients.add(client1);
        Server.clients.add(client2);

        Server.broadcast("Hello Clients!");

        assertTrue(client1Out.toString().contains("Hello Clients!"));
        assertTrue(client2Out.toString().contains("Hello Clients!"));
    }
}
