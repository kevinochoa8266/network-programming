// New code is at the bottom
import Message.*;
import Parser.PeerInfoParser;
import Parser.PeerInfoParser.PeerInfo;

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
public class Peer {
    private int myPeerId;
    private int peerId;
    private Socket socket;
    private byte[] bitfield;
    private boolean interested;
    public Server thisServer = null;



    public int getMyPeerId() {
        return myPeerId;
    }

    public void setMyPeerId(int myPeerId) {
        this.myPeerId = myPeerId;
    }

    public int getPeerId() {
        return peerId;
    }

    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }


    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public byte[] getBitfield() {
        return bitfield;
    }

    public void setBitfield(byte[] bitfield) {
        this.bitfield = bitfield;
    }

    public boolean isInterested() {
        return interested;
    }

    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    public void exchangeBitfields(){ // Uses the ogPeer and desiredPeer to exchange bitfields

    }

    public void sendMessageBitfield(Bitfield bitfield, Peer peer){

        try {
            ObjectOutputStream out = new ObjectOutputStream(peer.getSocket().getOutputStream());

            // send the message
            byte[] messageBytes = bitfield.getBytes();
            //length
            out.writeInt(messageBytes.length);
            // write the message out
            out.write(messageBytes);
            // force data to be sent
            out.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessageChoke(Choke choke, Peer peer){

        try {
            ObjectOutputStream out = new ObjectOutputStream(peer.getSocket().getOutputStream());

            // send the message
            byte[] messageBytes = choke.getBytes();
            //length
            out.writeInt(messageBytes.length);
            // write the message out
            out.write(messageBytes);
            // force data to be sent
            out.flush();
            }
            catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessageHave(Have have, Peer peer){

        try {
            ObjectOutputStream out = new ObjectOutputStream(peer.getSocket().getOutputStream());

            // send the message
            byte[] messageBytes = have.getBytes();
            //length
            out.writeInt(messageBytes.length);
            // write the message out
            out.write(messageBytes);
            // force data to be sent
            out.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessageInterested(Interested interested, Peer peer){

        try {
            ObjectOutputStream out = new ObjectOutputStream(peer.getSocket().getOutputStream());

            // send the message
            byte[] messageBytes = interested.getBytes();
            //length
            out.writeInt(messageBytes.length);
            // write the message out
            out.write(messageBytes);
            // force data to be sent
            out.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessageNotInterested(NotInterested notInterested, Peer peer){

        try {
            ObjectOutputStream out = new ObjectOutputStream(peer.getSocket().getOutputStream());

            // send the message
            byte[] messageBytes = notInterested.getBytes();
            //length
            out.writeInt(messageBytes.length);
            // write the message out
            out.write(messageBytes);
            // force data to be sent
            out.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessagePiece(Piece piece, Peer peer){

        try {
            ObjectOutputStream out = new ObjectOutputStream(peer.getSocket().getOutputStream());

            // send the message
            byte[] messageBytes = piece.getBytes();
            //length
            out.writeInt(messageBytes.length);
            // write the message out
            out.write(messageBytes);
            // force data to be sent
            out.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessageRequest(Request request, Peer peer){

        try {
            ObjectOutputStream out = new ObjectOutputStream(peer.getSocket().getOutputStream());

            // send the message
            byte[] messageBytes = request.getBytes();
            //length
            out.writeInt(messageBytes.length);
            // write the message out
            out.write(messageBytes);
            // force data to be sent
            out.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessageUnchoke(Unchoke unchoke, Peer peer){

        try {
            ObjectOutputStream out = new ObjectOutputStream(peer.getSocket().getOutputStream());

            // send the message
            byte[] messageBytes = unchoke.getBytes();
            //length
            out.writeInt(messageBytes.length);
            // write the message out
            out.write(messageBytes);
            // force data to be sent
            out.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    // OLD IMPLEMENTATION ABOVE



   // private ObjectOutputStream outputStream;
   // private ObjectInputStream inputStream;
    private boolean isConnected;
    private int origID;

    private PeerInfo peerInfo;
    public Peer(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }

    private Socket peerSocket;
// This is for OUTGOING connections
    public boolean initClientConnection(PeerInfoParser.PeerInfo origPeer, PeerInfoParser.PeerInfo desiredPeer){
        this.origID = origPeer.getPeerID();

        try (Socket newSocket = new Socket(desiredPeer.getHostName(), desiredPeer.getPortNumber())) {
            System.out.println(origPeer.getPeerID()+":"+ origPeer.getPortNumber() + " socket connected to " + desiredPeer.getPeerID() + ":" + desiredPeer.getPortNumber());
            peerSocket = newSocket;
            remotePeerId = desiredPeer.getPeerID();
            reversePerformHandshake(peerSocket);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* 
        try {


            Socket peerSocket = new Socket(peerinfo.getHostName(), peerinfo.getPortNumber());
            setSocket(peerSocket);
        } catch (IOException e) {
            e.printStackTrace();
            // connection error handle
        }
        
        */
        return false;
    }

    public Socket getSocket() {
        return peerSocket;
    }

    
    // Gets called from PeerServer, when a new connection is coming in.
    public void connect(Socket clientSocket) throws IOException {
        this.origID = peerInfo.getPeerID(); // Yes, even though this peer class is for the incoming peer, we initially give it the host's peer ID.
        this.peerSocket = clientSocket; // client socket is the incoming connection. This whole peer class is the incoming connection.
       // outputStream = new ObjectOutputStream(socket.getOutputStream());
        //inputStream = new ObjectInputStream(socket.getInputStream());
        isConnected = true;
    
        // Perform handshake. 
        performHandshake(clientSocket);
    }
    
    public void connect() throws IOException {
        // This method is used for outgoing connections
        connect(new Socket(peerInfo.getHostName(), peerInfo.getPortNumber()));
    }

    private int remotePeerId;

    public void setRemotePeerId(int remotePeerId) {
        this.remotePeerId = remotePeerId;
    }

    public int getRemotePeerId() {
        return remotePeerId;
    }

    private void receiveHandshake(Socket clientSocket) throws IOException {
        System.out.println(origID + " attempting to receiving handshake from " + remotePeerId);

        // Receive handshake message
        byte[] receivedBytes = null;
        try {
            ObjectInputStream inStream = new ObjectInputStream(clientSocket.getInputStream());
			receivedBytes = (byte[]) inStream.readObject();
            Handshake receivedHandshake = new Handshake(receivedBytes);
            /* 
            System.out.println("Got:" + receivedBytes.toString());
            System.out.println("Got:" + receivedHandshake.getHandshakeHeader());
            System.out.println("Got:" + receivedHandshake.getPeerId());
            */
            setRemotePeerId(receivedHandshake.getPeerId());
            // Now that we know the peer ID thats coming in, we can set the null peerInfo object equal to the one we have in the peerInfo list.
            PeerInfoParser peerInfoParser = new PeerInfoParser();
            peerInfoParser.readFile();
            //System.out.println(peerInfoParser.getPeerInfoList().toString());
            boolean success = false;
            for (PeerInfo peerInfo : peerInfoParser.getPeerInfoList()) {
               // System.out.println("Looked at Peer ID: " + peerInfo.getPeerID() + " against " + remotePeerId);
                if (peerInfo.getPeerID() == remotePeerId) {
                    this.peerInfo = peerInfo;
                    success = true;
                    System.out.println(origID + " successfully received handshake from " + remotePeerId);
                    break;
                }
            }
            if (success == false) {
                System.out.println("ALERT: Peer ID not found in peer info list. USING duplicate peer ID.");
            }
		} catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}
    }

    private void sendHandshake(Socket clientSocket) throws IOException {
        try {
            System.out.println(origID + " attempting to send handshake to " + remotePeerId);
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            Handshake handshakeMessage = new Handshake(origID);
            byte[] handshakeBytes = handshakeMessage.getBytes();
            out.writeObject(handshakeBytes);
            out.flush();
            System.out.println(origID + " sent handshake to " + remotePeerId);
        } catch (Exception e) {
            System.out.println("Error sending handshake message: " + e.getMessage());
        }
    }
    

    private void performHandshake(Socket clientSocket) throws IOException {
        // Receive handshake message
        receiveHandshake(clientSocket);

        // Send handshake message
        sendHandshake(clientSocket);
    }

    private void reversePerformHandshake(Socket clientSocket) throws IOException {
        // Send handshake message
        sendHandshake(clientSocket);

        // Receive handshake message
        receiveHandshake(clientSocket);
    }



    public void setPeerInfo(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }

    private FileManager fileManager;
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void handlePieceMessage(Piece pieceMessage) {
        // Extract piece index and data from the message
        int pieceIndex = pieceMessage.getPieceIndex();
        byte[] pieceData = pieceMessage.getPieceData();

        // Save the piece using FileManager
        try {
            fileManager.setPiece(pieceIndex, pieceData);
            // After saving, check if download is complete or request next piece
        } catch (IOException e) {
            System.err.println("Failed to save piece: " + e.getMessage());
        }
    }

    
    boolean isChoked; // TODO: IMPLEMENT
    public boolean getIsChoked() {
        return isChoked;
    }
    boolean isComplete = false;
    public boolean isCompleted() {
        return isComplete;
    }



}

