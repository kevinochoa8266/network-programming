/*
 * 
 * PeerProcess.java is the entry point for the program.
 * 
 * 1. It starts the server for the current peer. (The one that is running this program)
 * 2. It connects to earlier peers, and stores the successful connections in 
 *   the connectedPeers list.
 * 3. It listens for new connections, and creates a new thread to handle each new connection.
 * 
 * The main function calls peerProcess.start()
 * 
 * There are two main objects to be aware of. The PeerInfo object, and the Peer object.
 * Pearinfo comes from PeerInfoParser, and hold data like peerID, hostName, portNumber.
 * (and also a list of all peers from the PeerInfo.cfg file, (this list must be populated by PeerInfoParser.readFile() first)
 * 
 * The second main object is the Peer object. Every peer that this peer connects to will become a Peer object.
 * You control the connection to another Peer by calling it's Peer object functions.
 * 
 * Current State:
 * 
 * The program is capable of connecting to other peers based off my testing with ServerTestClient.java 
 * (which is a barebones simulation of a peer). Handshaking is working, and I am currently working on
 * getting peers (Copies of this code) to automatically connect to each other.
 * 
 * 
 */
import Parser.CommonParser;
import Parser.PeerInfoParser;
import Parser.PeerInfoParser.PeerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.io.File;

public class peerProcess {

    private int myPeerId;
    private ServerSocket serverSocket;
    private List<Peer> connectedPeers = new ArrayList<Peer>();
    PeerInfo myPeerInfo = null;
    private boolean allPeersHaveCompleteFile;

    private FileManager fileManager;

    public peerProcess(int peerId) {
        this.myPeerId = peerId;

        // Initialize FileManager after reading configuration
        initializeFileManager();
    }

    private void initializeFileManager() {
        PeerInfoParser peerInfoParser = new PeerInfoParser();
        peerInfoParser.readFile();
        CommonParser commonParser = new CommonParser();
        commonParser.readFile();

        for (PeerInfo peerInfo : peerInfoParser.getPeerInfoList()) {
            if (peerInfo.getPeerID() == myPeerId) {
                myPeerInfo = peerInfo;
                break;
            }
        }

        boolean hasCompleteFile = myPeerInfo.hasFile();
        String fileName = commonParser.getFileName();
        int fileSize = commonParser.getFileSize();
        int pieceSize = commonParser.getPieceSize();
        this.fileManager = new FileManager(myPeerId, fileName, fileSize, pieceSize, hasCompleteFile);
    }
    
    private void startFileExchange() {
        // Start threads for each connected peer to handle file exchange
        for (Peer peer : connectedPeers) {
            new Thread(() -> handleFileExchangeWithPeer(peer)).start();
        }

        // Start a separate thread to check if all peers have completed file download
        new Thread(this::checkAllPeersCompletion).start();
    }

    private void handleFileExchangeWithPeer(Peer peer) {
        // Exchange bitfield messages
        peer.exchangeBitfields();

        // Main loop for exchanging pieces with this peer
        while (!peer.isCompleted() && !allPeersHaveCompleteFile) {
            // Logic for requesting and sending pieces
            // This should be based on the peer's choking status and the pieces needed
        }
    }

    private void checkAllPeersCompletion() {
        while (!allPeersHaveCompleteFile) {
            allPeersHaveCompleteFile = connectedPeers.stream()
                                                     .allMatch(Peer::isCompleted);
            try {
                Thread.sleep(1000);  // Check periodically
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    

    // 1. This gets called as soon as the program starts.
    public void start() throws IOException {

        // Reads The PeerInfo.cfg file and common.cfg file
        PeerInfoParser peerInfoParser = new PeerInfoParser();
        peerInfoParser.readFile();


        // Finds the right PeerInfo object for the current peer, sets it to myPeerInfo
        for (PeerInfo peerInfo : peerInfoParser.getPeerInfoList()) {
            if (peerInfo.getPeerID() == myPeerId) {
                myPeerInfo = peerInfo;
                break;
            }
        }

        // Create a server
        serverSocket = new ServerSocket(myPeerInfo.getPortNumber());
        System.out.println("Server started on port " + myPeerInfo.getPortNumber());

        // Connect to earlier peers 
        connectToEarlierPeers();


        // Listen for new connections in a separate thread
        listenForNewConnections();

        // Start file exchange
        startFileExchange();
        
    }

    private void connectToEarlierPeers() {
        System.out.println("Connecting to earlier peers");
        PeerInfoParser peerInfoParser = new PeerInfoParser();
        peerInfoParser.readFile();
        for (PeerInfo peerInfo : peerInfoParser.getPeerInfoList()) {
            if (peerInfo.getPeerID() < myPeerId) {
                Peer desiredPeer = new Peer(peerInfo);
                boolean status = desiredPeer.initClientConnection(myPeerInfo, peerInfo);
                if (status == true) {
                    connectedPeers.add(desiredPeer);
                }else {
                    System.out.println("Failed to connect to peer " + peerInfo.getPeerID());
                }
            }
        }
        System.out.println("Done connecting to earlier peers");
    }


    private void listenForNewConnections() {
        System.out.println(myPeerId +" listening for new connections");
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
            Peer newPeer = new Peer(myPeerInfo); // Creates a Peer object for the incoming connection. Sets to this peer's number for now.

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
