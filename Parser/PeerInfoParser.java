package Parser;

import java.io.*;
import java.util.ArrayList;

public class PeerInfoParser {
    /*
    private int peerId;
    private String hostName;
    private int portNumber;
    private boolean hasFile;

     */
    private ArrayList<PeerInfo> peerInfoList = new ArrayList<>();
    private final String filename = "./PeerInfo.cfg";

    public void readFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] token = line.split(" ");

                int peerId = Integer.parseInt(token[0]);
                String hostName = token[1];
                int portNumber = Integer.parseInt(token[2]);
                boolean hasFile = "1".equals(token[3]);

                PeerInfo peerInfo = new PeerInfo(peerId, hostName, portNumber, hasFile);
                peerInfoList.add(peerInfo);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PeerInfo> getPeerInfoList(){
        return peerInfoList;
    }
    public ArrayList<Integer> getIdList() {

        /*
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
        */
        ArrayList<Integer> list = new ArrayList<>();
        for (PeerInfo peerInfo : peerInfoList) {
            list.add(peerInfo.getPeerID());
        }

        return list;
    }


    public static class PeerInfo {
        private int peerId;
        private String hostName;
        private int portNumber;
        private boolean hasFile;

        public PeerInfo(int peerId, String hostName, int portNumber, boolean hasFile) {
            this.peerId = peerId;
            this.hostName = hostName;
            this.portNumber = portNumber;
            this.hasFile = hasFile;
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

        public boolean hasFile() {
            return hasFile;
        }
    }
}
