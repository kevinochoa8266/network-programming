import Parser.CommonParser;
import Parser.PeerInfoParser;
import Parser.PeerInfoParser.PeerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class peerProcess {

    private int myPeerId;
    private ServerSocket serverSocket;
    private List<Peer> connectedPeers = new ArrayList<Peer>();
    PeerInfo myPeerInfo = null;

    public peerProcess(int peerId) {
        this.myPeerId = peerId;
    }

    public void start() throws IOException {

        // Reads The PeerInfo.cfg file and common.cfg file
        PeerInfoParser peerInfoParser = new PeerInfoParser();
        peerInfoParser.readFile();
        CommonParser commonParser = new CommonParser();
        commonParser.readFile();


        // Find the right PeerInfo object for the current peer, sets it to myPeerInfo
        for (PeerInfo peerInfo : peerInfoParser.getPeerInfoList()) {
            if (peerInfo.getPeerID() == myPeerId) {
                myPeerInfo = peerInfo;
                break;
            }
        }

        // Create a server
        serverSocket = new ServerSocket(myPeerInfo.getPortNumber());
        System.out.println("Server started on port " + myPeerInfo.getPortNumber());
        // Listen for new connections in a separate thread
        listenForNewConnections();
    }


    private void listenForNewConnections() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                // Handling the new connection in a separate thread
                new Thread(() -> handleNewConnection(clientSocket)).start();
            } catch (IOException e) {
                System.out.println("Error accepting new connection: " + e.getMessage());
                break;
            }
        }
    }
    
    // Whenever a new connection comes in, this handles it for the server.
    private void handleNewConnection(Socket clientSocket) {
        try {
            Peer newPeer = new Peer(null); // Creates a Peer object for the incoming connection. Does not set PeerInfo yet

            newPeer.connect(clientSocket); // Uses the Peer class to establish a connection and perform handshake. Passes in the socket.

            int connectedPeerId = newPeer.getRemotePeerId(); // Retrieve peer ID from handshake

            PeerInfoParser peerInfoParser = new PeerInfoParser(); // Declare and initialize peerInfoParser
            peerInfoParser.readFile(); // Read the peer info file

            List<PeerInfoParser.PeerInfo> peerInfoList = peerInfoParser.getPeerInfoList(); // Get the list of PeerInfo objects
            PeerInfoParser.PeerInfo peerInfo = null;
            for (PeerInfoParser.PeerInfo info : peerInfoList) {
                if (info.getPeerID() == connectedPeerId) {
                    peerInfo = info;
                    break;
                }
            }

            if (peerInfo != null) {
                newPeer.setPeerInfo(peerInfo); // Set the correct PeerInfo
                connectedPeers.add(newPeer);
                // TODO: Handle initial communication with the new peer
            } else {
                System.out.println("PeerInfo not found for connected peer: " + connectedPeerId);
            }
        } catch (IOException e) {
            System.out.println("Error handling new connection: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }





    public static void main(String[] args) {
        // Parse arguments and start the peer process
        if (args.length != 1) {
            System.out.println("Usage: java Main <peerID>");
            System.exit(1);
        }

        int peerId = Integer.parseInt(args[0]);

        //peerProcess peerProcess = new peerProcess(peerId);

        try {
            peerProcess peerProcess = new peerProcess(peerId);
            peerProcess.start();
        } catch (Exception e) {
            System.out.println("Failed to start the peer process: " + e.getMessage());
        }
    }
}




    /* 
    public static void main(String[] args) {

        System.out.println("Startup");

        // read.File() function to read in the config files (Common & PeerInfo)
        PeerInfoParser peerInfoParser = new PeerInfoParser();
        peerInfoParser.readFile();
        CommonParser commonParser = new CommonParser();
        commonParser.readFile();

        // current peer
        int myPeerID = Integer.parseInt(args[0]);

        // new obj for current peer
        Peer currPeer = new Peer();
        currPeer.setMyPeerId(myPeerID);

        List<PeerInfo> peerInfoList = peerInfoParser.getPeerInfoList();

        for (PeerInfo peerinfo : peerInfoList) {
            if (peerinfo.getPeerID() < myPeerID) {
                // Assuming you have a method to establish a connection with a peer
                currPeer.initConnection(peerinfo);
            }
        }


    }
    */
