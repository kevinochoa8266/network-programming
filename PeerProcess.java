import Parser.CommonParser;
import Parser.PeerInfoParser;

import java.util.ArrayList;

public class PeerProcess {

    private static int myPeerId;
    private static String hostName;
    private static int portNumber;
    private Handshake myHandshake;
    // Peers
    ArrayList<Peer> peers = new ArrayList<Peer>();

    public static void main(String[] args) {
        System.out.println("Startup");
        setVals();
    }

    public static void setVals() {
        PeerInfoParser peerInfoParser = new PeerInfoParser();
        peerInfoParser.readFile();
        CommonParser commonParser = new CommonParser();
        commonParser.readFile();
        myPeerId = peerInfoParser.getPeerID();
        hostName = peerInfoParser.getHostName();
        portNumber = peerInfoParser.getPortNumber();
    }

    // Create the HandShake header for this peer
    public void createMyHandshake() {
        myHandshake = new Handshake(myPeerId);
    }

    public void connect() {
        // Connect to peers that precede me.

    }


}
