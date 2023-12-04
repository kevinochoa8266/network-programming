import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private final String logFilePath;

    public Logger(int peerId) {
        // Define the directory name using the peer ID
        String peerDirectoryName = "peer_" + peerId;

        // Get the system's current working directory
        String workingDir = System.getProperty("user.dir");

        // Construct the directory path for the peer
        Path peerDirectoryPath = Paths.get(workingDir, peerDirectoryName);

        // Ensure the directory exists
        try {
            Files.createDirectories(peerDirectoryPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create directory for peer logs: " + peerDirectoryPath, e);
        }

        // Construct the full log file path
        this.logFilePath = peerDirectoryPath.resolve("log_peer_" + peerId + ".log").toString();
    }

    private synchronized void logMessage(String message) {

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)))) {
            out.println(getCurrentTime() + ": " + message);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the log file: " + e.getMessage());
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    public void logTcpConnection(int localPeerId, int remotePeerId) {
        logMessage("Peer " + localPeerId + " makes a connection to Peer " + remotePeerId + ".");
    }

    public void logTcpConnectionFrom(int localPeerId, int remotePeerId) {
        logMessage("Peer " + localPeerId + " is connected from Peer " + remotePeerId + ".");
    }

    public void logChangeOfPreferredNeighbors(int peerId, String neighborList) {
        logMessage("Peer " + peerId + " has the preferred neighbors " + neighborList + ".");
    }

    public void logChangeOfOptimisticallyUnchokedNeighbor(int peerId, int optimisticNeighborId) {
        logMessage("Peer " + peerId + " has the optimistically unchoked neighbor " + optimisticNeighborId + ".");
    }

    public void logUnchoking(int localPeerId, int remotePeerId) {
        logMessage("Peer " + localPeerId + " is unchoked by " + remotePeerId + ".");
    }

    public void logChoking(int localPeerId, int remotePeerId) {
        logMessage("Peer " + localPeerId + " is choked by " + remotePeerId + ".");
    }

    public void logReceivingHave(int localPeerId, int remotePeerId, int pieceIndex) {
        logMessage("Peer " + localPeerId + " received the ‘have’ message from " + remotePeerId + " for the piece " + pieceIndex + ".");
    }

    public void logReceivingInterested(int localPeerId, int remotePeerId) {
        logMessage("Peer " + localPeerId + " received the ‘interested’ message from " + remotePeerId + ".");
    }

    public void logReceivingNotInterested(int localPeerId, int remotePeerId) {
        logMessage("Peer " + localPeerId + " received the ‘not interested’ message from " + remotePeerId + ".");
    }

    public void logDownloadingPiece(int localPeerId, int remotePeerId, int pieceIndex, int numberOfPieces) {
        logMessage("Peer " + localPeerId + " has downloaded the piece " + pieceIndex + " from " + remotePeerId + ". Now the number of pieces it has is " + numberOfPieces + ".");
    }

    public void logCompletionOfDownload(int peerId) {
        logMessage("Peer " + peerId + " has downloaded the complete file.");
    }
}