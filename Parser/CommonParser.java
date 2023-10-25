package Parser;

import java.io.*;
import java.util.ArrayList;

public class CommonParser {
    private int numberOfPreferredNeighbors;
    private int unchokingInterval;
    private int optimisticUnchokingInterval;
    private String fileName;
    private int fileSize;
    private int pieceSize;
    private boolean hasFile;

    private final String filename = "Common.cfg";

    public void readFile() {
        try {
            //System.out.println(new File(".").getAbsolutePath());
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            line = reader.readLine();
            String[] token = line.split(" ");
            numberOfPreferredNeighbors = Integer.parseInt(token[1]);
            line = reader.readLine();
            token = line.split(" ");
            unchokingInterval = Integer.parseInt(token[1]);
            line = reader.readLine();
            token = line.split(" ");
            optimisticUnchokingInterval = Integer.parseInt(token[1]);
            line = reader.readLine();
            token = line.split(" ");
            fileName = token[1];
            line = reader.readLine();
            token = line.split(" ");
            fileSize = Integer.parseInt(token[1]);
            line = reader.readLine();
            token = line.split(" ");
            pieceSize = Integer.parseInt(token[1]);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Enable for Debugging
        printVals();
    }


    public int getNumberOfPreferredNeighbors() {
        return numberOfPreferredNeighbors;
    }
    public int getUnchokingInterval() {
        return unchokingInterval;
    }
    public int getOptimisticUnchokingInterval() {
        return optimisticUnchokingInterval;
    }
    public String getFileName() {
        return fileName;
    }
    public int getFileSize() {
        return fileSize;
    }
    public int getPieceSize() {
        return pieceSize;
    }
    public boolean hasFile() {
        return hasFile;
    }

    // EARLY DEBUGGING
    public void printVals() {
        System.out.println("numberOfPreferredNeighbors: " + numberOfPreferredNeighbors);
        System.out.println("unchokingInterval: " + unchokingInterval);
        System.out.println("optimisticUnchokingInterval: " + optimisticUnchokingInterval);
        System.out.println("fileName: " + fileName);
        System.out.println("fileSize: " + fileSize);
        System.out.println("pieceSize: " + pieceSize);
    }

}
