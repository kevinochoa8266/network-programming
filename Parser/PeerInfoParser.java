package Parser;

import java.io.*;
import java.util.ArrayList;

public class PeerInfoParser {

    private int peerId;
    private String hostName;
    private int portNumber;
    private boolean hasFile;

    private final String filename = "./PeerInfo.cfg";

    public void readFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] token = line.split(" ");

                peerId = Integer.parseInt(token[0]);
                hostName = token[1];
                portNumber = Integer.parseInt(token[2]);
                hasFile = "1".equals(token[3]);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Enable for Debugging
        //printVals();
    }

    public ArrayList<Integer> getIdList() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] token = line.split(" ");
                list.add(Integer.parseInt(token[0]));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    public int getPeerID() {
        return peerId;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public boolean HasFile() {
        return hasFile;
    }
    // EARLY DEBUGGING
    public void printVals() {
        System.out.println("PeerID: " + peerId);
        System.out.println("HostName: " + hostName);
        System.out.println("PortNumber: " + portNumber);
        System.out.println("HasFile: " + hasFile);

    }

}
