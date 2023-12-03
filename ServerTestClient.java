import java.io.*;
import java.net.*;
import java.util.Arrays;

public class ServerTestClient {
    private String host;
    private int port;
    private int remotePeerId;

    public ServerTestClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void testConnection() {
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to server on port " + port);

            // Using ObjectOutputStream and ObjectInputStream
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
    
            /* 
            // Send a test message
            String message = "Hello from client " + socket.getLocalSocketAddress();
            out.writeObject(message);
            out.flush();
            */


            // Send a test Handshake
            Handshake handshake = new Handshake(1002);
            System.out.println("Sending handshake: " + handshake.getHandshakeHeader() + handshake.getPeerId());
            System.out.println("Raw value: " + Arrays.toString(handshake.getBytes()));
            byte[] handshakeBytes = handshake.getBytes();
            out.writeObject(handshakeBytes);
            out.flush();
            System.out.println("Sent handshake");

 ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            // Receive a test Handshake
            try {
                Object response = in.readObject();
                if (response instanceof byte[]) {
                    Handshake outHandshake = new Handshake((byte[]) response);
                    System.out.println("Server says: " + outHandshake.getHandshakeHeader());
                    System.out.println("Server says: " + outHandshake.getPeerId());
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found: " + e.getMessage());
            }


            // Read and print the response from the server
            byte[] receivedBytes = null;
            try {
                ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			    receivedBytes = (byte[]) inStream.readObject();
                Handshake receivedHandshake = new Handshake(receivedBytes);
                System.out.println("Got:" + receivedBytes.toString());
                System.out.println("Got:" + receivedHandshake.getHandshakeHeader());
                System.out.println("Got:" + receivedHandshake.getPeerId());
                remotePeerId = (receivedHandshake.getPeerId());
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found: " + e.getMessage());
            }

            // Close the connection
            socket.close();
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Adjust port number as necessary
        ServerTestClient client = new ServerTestClient("localhost", 6008);
        client.testConnection();
    }
}
