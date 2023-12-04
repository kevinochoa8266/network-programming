
import Parser.CommonParser;
import Parser.PeerInfoParser;
import Parser.PeerInfoParser.PeerInfo;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.io.File;
import java.util.stream.Collectors;

public class peerProcess {

    private int myPeerId;
    private ServerSocket serverSocket;
    private List<Peer> connectedPeers = new ArrayList<Peer>();
    PeerInfo myPeerInfo = null;
    private boolean allPeersHaveCompleteFile;
    public int unchokingInterval;
    public int optimisticUnchokingInterval;
    private Peer optimisticallyUnchokedPeer;
    private final List<Peer> preferredNeighbors = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<RateRecord> downloadRates = new ArrayList<>();
    private final Random random = new Random();
    private int numPreferredNeighbors;
    private Logger logger;


    private FileManager fileManager;

    public peerProcess(int peerId) {
        this.myPeerId = peerId;
        logger = new Logger(myPeerId);
        CommonParser commonParser = new CommonParser();
        commonParser.readFile();
        numPreferredNeighbors = commonParser.getNumberOfPreferredNeighbors();
        initializeFileManager();
    }

    private void initializeFileManager() {
        PeerInfoParser peerInfoParser = new PeerInfoParser();
        peerInfoParser.readFile();
        CommonParser commonParser = new CommonParser();
        commonParser.readFile();
        unchokingInterval = commonParser.getUnchokingInterval();
        optimisticUnchokingInterval = commonParser.getOptimisticUnchokingInterval();
        
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
        try {
            this.fileManager = new FileManager(myPeerId, fileName, fileSize, pieceSize, hasCompleteFile);
        } catch (Exception e) {
            System.out.println("Error initializing file manager: " + e.getMessage());
        }
    }
    private void initPreferredNeighbors() {
        if (preferredNeighbors.isEmpty() && connectedPeers.size() > 0) {
            // If preferred neighbors list is empty, make initial selection
            preferredNeighbors.addAll(connectedPeers.subList(0, Math.min(numPreferredNeighbors, connectedPeers.size())));
            for (Peer peer : preferredNeighbors) {
                try {
                    unchokePeer(peer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.logChangeOfPreferredNeighbors(myPeerId, preferredNeighbors.stream()
        .map(p -> String.valueOf(p.getPeerId()))
        .collect(Collectors.joining(",")));
        
    }

    private void selectOptimistically() {
        // Your implementation here
    }

    private void startFileExchange() {
        // Initialize preferred neighbors
        initPreferredNeighbors();
        
        // Start threads for each connected peer to handle file exchange
        int UnchokedNeighbor = 0; // Define UnchokedNeighbor variable

        scheduler.scheduleAtFixedRate(() -> {
            try {
                reselectPreferredNeighbors();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }, 0, unchokingInterval, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::selectOptimistically, 0, optimisticUnchokingInterval, TimeUnit.SECONDS);
        for (Peer peer : connectedPeers) {
            new Thread(() -> {
                try {
                    handleFileExchangeWithPeer(peer);
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }).start();
        }


        // Start a separate thread to check if all peers have completed file download
        new Thread(this::checkAllPeersCompletion).start();
    }

    private void reselectPreferredNeighbors() throws IOException {
        // Sort the downloadRates list based on rates, from high to low
        downloadRates.sort((a, b) -> Long.compare(b.getRate(), a.getRate()));

        List<Peer> newPreferredNeighbors = new ArrayList<>();
        for(int i = 0; i < Math.min(numPreferredNeighbors, downloadRates.size()); i++) {
            int peerId = downloadRates.get(i).getPeerId();
            connectedPeers.stream()
                .filter(p -> p.getPeerId() == peerId)
                .findFirst()
                .ifPresent(newPreferredNeighbors::add);
        }

        // Unchoke new preferred neighbors and choke others
        for (Peer connectedPeer : connectedPeers) {
            if (newPreferredNeighbors.contains(connectedPeer)) {
                unchokePeer(connectedPeer);
            } else {
                chokePeer(connectedPeer);
            }
        }

        // Update the list of preferred neighbors
        preferredNeighbors.clear();
        preferredNeighbors.addAll(newPreferredNeighbors);
    }
    
    private void selectOptimisticallyUnchokedNeighbor() throws IOException {
        List<Peer> chokedInterestedPeers = connectedPeers.stream()
            .filter(Peer::isInterested) // Only interested peers
            .filter(p -> !preferredNeighbors.contains(p)) // Not already in preferred neighbors
            .collect(Collectors.toList());

        if (!chokedInterestedPeers.isEmpty()) {
            int randomIndex = random.nextInt(chokedInterestedPeers.size());
            Peer selectedPeer = chokedInterestedPeers.get(randomIndex);

            if (optimisticallyUnchokedPeer != null) {
                chokePeer(optimisticallyUnchokedPeer); // Choke previously optimistically unchoked peer
            }
            optimisticallyUnchokedPeer = selectedPeer;
            unchokePeer(optimisticallyUnchokedPeer); // Unchoke the new optimistically unchoked peer
        }
        if (optimisticallyUnchokedPeer != null) {
            logger.logChangeOfOptimisticallyUnchokedNeighbor(myPeerId, optimisticallyUnchokedPeer.getPeerId());
        }
    }
    
    private void chokePeer(Peer peer) throws IOException {
        peer.sendChokeMessage();
    }
    
    private void unchokePeer(Peer peer) throws IOException {
        peer.sendUnchokeMessage();
    }

    private void updateDownloadRate(int peerId, long downloadedBytes) {
        RateRecord rateRecord = downloadRates.stream()
                                             .filter(r -> r.getPeerId() == peerId)
                                             .findFirst()
                                             .orElse(new RateRecord(peerId));
        // Assuming 'downloadedBytes' is the amount of data downloaded since the last rate update
        rateRecord.setRate(downloadedBytes); // Set the new download rate
    }

    private void resetDownloadRates() {
        downloadRates.clear();
    }

    private void onPieceDownloaded(Peer fromPeer, int pieceIndex, byte[] pieceData) throws IOException {
        fileManager.setPiece(pieceIndex, pieceData);
        RateRecord rateRecord = downloadRates.stream()
            .filter(r -> r.getPeerId() == fromPeer.getPeerId())
            .findFirst()
            .orElseGet(() -> {
                RateRecord newRecord = new RateRecord(fromPeer.getPeerId());
                downloadRates.add(newRecord);
                return newRecord;
            });
        rateRecord.addDownloadedBytes(pieceData.length);
    }


    private void handleFileExchangeWithPeer(Peer peer) throws ClassNotFoundException, IOException {
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
        // We should probably modify to create a peer for self and have the var in peerProcess.
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
        //System.out.println("Connecting to earlier peers");
        PeerInfoParser peerInfoParser = new PeerInfoParser();
        peerInfoParser.readFile();
        for (PeerInfo peerInfo : peerInfoParser.getPeerInfoList()) {
            if (peerInfo.getPeerID() < myPeerId) {
                Peer desiredPeer = new Peer(peerInfo);
                boolean status = desiredPeer.initClientConnection(myPeerInfo, peerInfo);
                if (status == true) {
                    connectedPeers.add(desiredPeer);
                    logger.logTcpConnection(Integer.valueOf(myPeerId), desiredPeer.getPeerId());
                } else {
                    //System.out.println("Failed to connect to peer " + peerInfo.getPeerID());
                }
            }
        }
        //System.out.println("Done connecting to earlier peers");
    }


    private void listenForNewConnections() {
        //System.out.println(myPeerId +" listening for new connections");
        while (true) {
            
                Socket clientSocket;
                try {
                    clientSocket = serverSocket.accept();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // Handling the new connection in a separate thread
                new Thread(() -> {
                    try {
                        final Socket finalClientSocket = serverSocket.accept(); // Declare and initialize the final variable
                        handleNewConnection(finalClientSocket);
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }).start();

        }
    }
        
    
    // Whenever a new connection comes in, this handles it for the server.
    private void handleNewConnection(Socket clientSocket) throws ClassNotFoundException, IOException {
        try {
            // Perform the handshake and exchange bitfields
            Peer newPeer = new Peer(myPeerId, clientSocket); // Pass the current peer's ID and client socket
            boolean handshakeSuccess = true; // FIXME ASAP isHandshakeSuccessful = newPeer.performHandshake();

            if (handshakeSuccess) {
                // using clientsocket.port, figure out the peerID of the peer that connected to you

                logger.logTcpConnectionFrom(myPeerId, newPeer.getRemotePeerId());
                // Successfully performed handshake, now exchange bitfields
                newPeer.exchangeBitfields(); // Initiate bitfield exchange

                // Retrieve the remote peer ID from the handshake
                int connectedPeerId = newPeer.getRemotePeerId();

                // Access the peer info list and find the PeerInfo for the remote peer ID
                PeerInfoParser peerInfoParser = new PeerInfoParser();
                peerInfoParser.readFile();
                PeerInfo remotePeerInfo = peerInfoParser.getPeerInfoList().get(connectedPeerId);
                if (remotePeerInfo != null) {
                    // Set remote peer information and start handling file exchange
                    newPeer.setPeerInfo(remotePeerInfo);
                    connectedPeers.add(newPeer); // Add the peer to the list of connected peers
                    new Thread(() -> {
                        try {
                            handleFileExchangeWithPeer(newPeer);
                        } catch (ClassNotFoundException | IOException e) {
                            
                            e.printStackTrace();
                        }
                    }).start();
                } else {
                    //System.out.println("PeerInfo not found for connected peer ID: " + connectedPeerId);
                }
            } else {
                //System.out.println("Handshake failed with peer using socket: " + clientSocket);
            }
        } catch (IOException | ClassNotFoundException e) {
           // System.out.println("Error handling new connection: " + e.getMessage());
        }
    }

    private void registerPeer(Peer newPeer, Socket clientSocket) {
        connectedPeers.add(newPeer);
        new Thread(() -> {
            try {
                handleFileExchangeWithPeer(newPeer);
            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }).start();
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
    
    public static class RateRecord {
        private final int peerId;
        private long downloadedBytesSinceLastInterval;
        private long cumulativeBytes;

        public RateRecord(int peerId) {
            this.peerId = peerId;
            this.downloadedBytesSinceLastInterval = 0;
            this.cumulativeBytes = 0;
        }

        public int getPeerId() {
            return peerId;
        }

        public synchronized void setRate(long bytes) {
            downloadedBytesSinceLastInterval = bytes;
        }

        public long getRate() {
            // Returns the download rate since the last update
            return downloadedBytesSinceLastInterval;
        }

        public synchronized void addDownloadedBytes(long bytes) {
            downloadedBytesSinceLastInterval += bytes;
            cumulativeBytes += bytes;
        }

        public synchronized void resetRate() {
            downloadedBytesSinceLastInterval = 0;
        }
    }

    


}



