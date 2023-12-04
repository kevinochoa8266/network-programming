import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.BitSet;

public class FileManager {
    private final Path filePath; 
    private final int fileSize;
    private final int pieceSize;
    private final BitSet bitfield;
    private final boolean hasFileInitially;
    private Logger logger;
    private int peerID;

    public FileManager(int peerId, String fileName, int fileSize, int pieceSize, boolean hasFileInitially) {
        //System.out.println("FileManager: peerId=" + peerId + ", fileName=" + fileName + ", fileSize=" + fileSize + ", pieceSize=" + pieceSize + ", hasFileInitially=" + hasFileInitially);
        logger = new Logger(peerId);
        this.peerID = peerId;
        // Get the directory of the current Java file
        Path currentDir = Paths.get("").toAbsolutePath();
        //System.out.println("Current directory: " + currentDir);


        String directoryName = "peer_" + peerId;
        this.filePath = Paths.get(currentDir.toString(), directoryName, fileName);
        
        // Create directory
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create directories for peer " + peerId, e);
        } catch (Exception e) {
            System.out.println("FileManager: error creating directories for peer " + peerId + ": " + e.getMessage());
        }
       // System.out.println("FileManager: filePath=" + filePath);
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
        this.bitfield = new BitSet(getNumberOfPieces());
        this.hasFileInitially = hasFileInitially;

        File file = filePath.toFile();
        if (hasFileInitially) {
            initializeBitfieldForCompleteFile();
            try {
                splitFileIntoPieces();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            clearBitfield();
        }
    }

    private void initializeBitfieldForCompleteFile() {
        bitfield.set(0, getNumberOfPieces());
    }

    private void clearBitfield() {
        bitfield.clear();
    }

    public int getNumberOfPieces() {
        return (int) Math.ceil((double) fileSize / pieceSize);
    }

    public synchronized boolean hasPiece(int pieceIndex) {
        return bitfield.get(pieceIndex);
    }
    public synchronized void setPiece(int pieceIndex, byte[] data) throws IOException {
        if (!bitfield.get(pieceIndex)) {
            writeFilePiece(pieceIndex, data);
            bitfield.set(pieceIndex);
            logger.logDownloadingPiece(peerID, peerID+1, pieceIndex, bitfield.cardinality());
            if (isDownloadComplete()) {
                logger.logCompletionOfDownload(peerID);
            }
        }
    }

    public synchronized byte[] getPiece(int pieceIndex) throws IOException {
        if (!bitfield.get(pieceIndex)) {
            return null;
        }
        return readFilePiece(pieceIndex);
    }

    private void splitFileIntoPieces() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r")) {
            for (int pieceIndex = 0; pieceIndex < getNumberOfPieces(); pieceIndex++) {
                raf.seek((long) pieceIndex * pieceSize);
                byte[] data = new byte[Math.min(pieceSize, fileSize - pieceIndex * pieceSize)];
                raf.readFully(data);
                setPiece(pieceIndex, data);
            }
        } catch (IOException e) {
            throw new IOException("Error splitting file into pieces", e);
        }   
    }

    private void writeFilePiece(int pieceIndex, byte[] data) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            raf.seek((long) pieceIndex * pieceSize);
            raf.write(data, 0, Math.min(data.length, pieceSize));
        }
    }

    private byte[] readFilePiece(int pieceIndex) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r")) {
            raf.seek((long) pieceIndex * pieceSize);
            byte[] data = new byte[Math.min(pieceSize, fileSize - pieceIndex * pieceSize)];
            raf.readFully(data);
            return data;
        }
    }

    public synchronized boolean isDownloadComplete() {
        return bitfield.cardinality() == getNumberOfPieces();
    }

    public synchronized void mergeFile() throws IOException {
        if (isDownloadComplete()) {
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                for (int pieceIndex = 0; pieceIndex < getNumberOfPieces(); pieceIndex++) {
                    fos.write(getPiece(pieceIndex));
                }
            }
        }
    }

    public BitSet getBitfield() {
        return (BitSet) bitfield.clone();
    }

    public boolean hasPieces() {
        return bitfield.cardinality() > 0;
    }

    public synchronized boolean isInterested(BitSet receivedBitfield) {
        // Returns true if there exists at least one bit set to true in the receivedBitfield
        // that is set to false in this peer's bitfield (meaning this peer is missing the piece)
        BitSet clone = (BitSet) bitfield.clone();
        clone.flip(0, getNumberOfPieces()); // Flip bitfield to mark the missing pieces with true
        clone.and(receivedBitfield); // Perform logical AND with received bitfield
        return clone.cardinality() > 0;  // If there's at least one bit set, then this peer is interested
    }
}


