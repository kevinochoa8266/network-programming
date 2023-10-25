import Parser.CommonParser;
import Parser.PeerInfoParser;
import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class PeerProcess {

    private static int myPeerId;
    private static String hostName;
    private static int portNumber;
    private Handshake myHandshake;
    // Peers
    ArrayList<Peer> peers = new ArrayList<Peer>();

    public static void main(String[] args) {
        System.out.println("Startup");
        // First arg is the peer ID of this PeerProcess
        // myPeerId = Integer.parseInt(args[0]);
        // For testing purposes, we assign Peer ID 1001 to this PeerProcess
        myPeerId = 1001;
        setVals();
    }

    public static void setVals() {
        PeerInfoParser peerInfoParser = new PeerInfoParser();
        peerInfoParser.readFile();
        CommonParser commonParser = new CommonParser();
        commonParser.readFile();
        hostName = peerInfoParser.getHostName();
        portNumber = peerInfoParser.getPortNumber();
        // Call File Handler once it is implemented
        // fileHandler();
    }

    // Create the HandShake header for this peer
    public void createMyHandshake() {
        myHandshake = new Handshake(myPeerId);
    }

    public void connect() {
        // Connect to peers that precede me.
        // TODO: Have an array of all the peers and update this loop accordingly.
        int currentPeerId = 1001;
        while (myPeerId < currentPeerId) {
            // Connect to the peer
            // TODO: Create an Array of Peers instead of create a new Peer object every time.
            Peer currentPeer = peers.get(currentPeerId);
            try {
                Socket socket = new Socket(hostName, portNumber);
                System.out.println("Connected to " + hostName + " in port " + portNumber);
                // Initialize inputStream and outputStream
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                // Send the handshake to the peer
                out.writeObject(myHandshake);
                out.flush();
                // Receive handshake
                Handshake handshake = (Handshake) in.readObject();
                System.out.println("Received handshake from " + currentPeer.getPeerId());

            } catch (Exception e) {
                System.out.println(e);
            }

            // currentPeerId.next or something
        }
    }

    public void fileHandler() {
        // See if directory for this peer exists, and if not, create it.
        if (!new File("peer_" + myPeerId).exists()) {
            new File("peer_" + myPeerId).mkdir();
        }
        // Check if this peer has the file. (If so, update hasFile in PeerInfo.cfg).

        /* TODO: Implement HasFile() check
        if (PeerProcess.HasFile()) {

        }
        */

    }


}
