import Parser.CommonParser;
import Parser.PeerInfoParser;

public class PeerProcess {
    public static void main(String[] args) {

        System.out.println("Startup");
        //PeerInfoParser peerInfoParser = new PeerInfoParser();
        //peerInfoParser.readFile();
        CommonParser commonParser = new CommonParser();
        commonParser.readFile();
    }
}
