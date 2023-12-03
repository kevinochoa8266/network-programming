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

    public FileManager(int peerId, String fileName, int fileSize, int pieceSize, boolean hasFileInitially) {
        // Updated file path to include peer-specific subdirectory
        String directoryName = "peer_" + peerId;
        this.filePath = Paths.get(System.getProperty("user.home"), "project", directoryName, fileName);
        
        // Ensure the directory exists
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create directories for peer " + peerId, e);
        }

        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
        this.bitfield = new BitSet(getNumberOfPieces());
        this.hasFileInitially = hasFileInitially;

        File file = filePath.toFile();
        if (hasFileInitially) {
            initializeBitfieldForCompleteFile();
            splitFileIntoPieces();
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
        }
    }

    public synchronized byte[] getPiece(int pieceIndex) throws IOException {
        if (!bitfield.get(pieceIndex)) {
            return null;
        }
        return readFilePiece(pieceIndex);
    }

    private void splitFileIntoPieces() {
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r")) {
            for (int pieceIndex = 0; pieceIndex < getNumberOfPieces(); pieceIndex++) {
                raf.seek((long) pieceIndex * pieceSize);
                byte[] data = new byte[Math.min(pieceSize, fileSize - pieceIndex * pieceSize)];
                raf.readFully(data);
                setPiece(pieceIndex, data);
            }
        } catch (IOException e) {
            System.out.println("Error splitting file into pieces: " + e.getMessage());
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
}
