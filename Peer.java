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

    public Socket getSocket() {
        return socket;
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

    public void doHandshake(Peer peer){
        try {
            Handshake handshake = new Handshake(myPeerId);
            ObjectOutputStream out = new ObjectOutputStream(peer.getSocket().getOutputStream());


            // send handshake msg
            byte[] handshakeBytes = handshake.getBytes();
            out.writeInt(handshakeBytes.length);
            out.write(handshakeBytes);
            out.flush();

            // check response
            boolean isValidResponse = receiveHandshakeResponse(peer);
            if (isValidResponse) {
                // handle
            }
            else{
                //handle
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean receiveHandshakeResponse(Peer peer){
        try {
            ObjectInputStream in = new ObjectInputStream(peer.getSocket().getInputStream());

            // length of message coming in
            int messageLength = in.readInt();

            // read bytes coming in
            byte[] responseBytes = new byte[messageLength];
            in.readFully(responseBytes);

            Handshake responseHandshake = new Handshake(responseBytes);

            boolean isValidResponse = validateHandshakeResponse(responseHandshake);

            if (isValidResponse){
                //send bitfield message

            }
            else{
                // handle
            }

            return isValidResponse;

        }
        catch(IOException e){
            e.printStackTrace();
            return false;
        }

    }

    public boolean validateHandshakeResponse(Handshake responseHandshake){
        boolean isValidHeader = responseHandshake.getHandshakeHeader().equals("P2PFILESHARINGPROJ");
        boolean isValidPeerId = responseHandshake.getPeerId() != 0;
        boolean isValidResponse = isValidHeader && isValidPeerId;

        return isValidResponse;
    }

    public void exchangeBitfields(Peer peer){

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

    public void initConnection(PeerInfoParser.PeerInfo peerinfo){
        try {
            Socket peerSocket = new Socket(peerinfo.getHostName(), peerinfo.getPortNumber());
            setSocket(peerSocket);
        } catch (IOException e) {
            e.printStackTrace();
            // connection error handle
        }
    }

    public void initServer(PeerInfoParser.PeerInfo peerinfo){
        try {
            thisServer = new Server();
            Socket peerSocket = new Socket(peerinfo.getHostName(), peerinfo.getPortNumber());
            setSocket(peerSocket);
        } catch (IOException e) {
            e.printStackTrace();
            // connection error handle
        }
    }
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private boolean isConnected;

    private PeerInfo peerInfo;
    public Peer(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }
    
    public void connect(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        isConnected = true;
    
        // Perform handshake
       // performHandshake();
    }
    
    public void connect() throws IOException {
        // This method is used for outgoing connections
        connect(new Socket(peerInfo.getHostName(), peerInfo.getPortNumber()));
    }

    private static final int HANDSHAKE_HEADER_LENGTH = 18;
    private static final int ZERO_BITS_LENGTH = 10;
    private static final int PEER_ID_LENGTH = 4;
    private int remotePeerId;

    public void setRemotePeerId(int remotePeerId) {
        this.remotePeerId = remotePeerId;
    }

    public int getRemotePeerId() {
        return remotePeerId;
    }

    private void performHandshake() throws IOException {
        // Send handshake message
        Handshake handshakeMessage = new Handshake(peerInfo.getPeerID());

        outputStream.writeObject(handshakeMessage.getBytes());
        outputStream.flush();

        // Receive handshake message
        int responseLength = HANDSHAKE_HEADER_LENGTH + ZERO_BITS_LENGTH + PEER_ID_LENGTH;
        byte[] responseBytes = new byte[responseLength];
        inputStream.readFully(responseBytes);

        Handshake responseHandshake = new Handshake(responseBytes);
        if (!handshakeMessage.getHandshakeHeader().equals(responseHandshake.getHandshakeHeader())) {
            throw new IOException("Invalid handshake header received.");
        }

        // Store the remote peer ID
        setRemotePeerId(responseHandshake.getPeerId());
    }
    
    public void setPeerInfo(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }















}

