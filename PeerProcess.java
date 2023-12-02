import Parser.CommonParser;
import Parser.PeerInfoParser;
import Parser.PeerInfoParser.PeerInfo;

import java.util.List;

public class PeerProcess {
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
}