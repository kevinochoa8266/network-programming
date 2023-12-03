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

    // OLD IMPLEMENTATION ABOVE



   // private ObjectOutputStream outputStream;
   // private ObjectInputStream inputStream;
    private boolean isConnected;
    private int origID;

    private PeerInfo peerInfo;
    public Peer(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }
    
    // Gets called from PeerServer, when a new connection is coming in.
    public void connect(Socket clientSocket) throws IOException {
        this.origID = peerInfo.getPeerID(); // Yes, even though this peer class is for the incoming peer, we initially give it the host's peer ID.
        this.socket = clientSocket; // client socket is the incoming connection. This whole peer class is the incoming connection.
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
    

    private void performHandshake(Socket clientSocket) throws IOException {
        System.out.println("Performing handshake with peer ");

        // Receive handshake message
        byte[] receivedBytes = null;
        try {
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			receivedBytes = (byte[]) inStream.readObject();
            Handshake receivedHandshake = new Handshake(receivedBytes);
            System.out.println("Got:" + receivedBytes.toString());
            System.out.println("Got:" + receivedHandshake.getHandshakeHeader());
            System.out.println("Got:" + receivedHandshake.getPeerId());
            setRemotePeerId(receivedHandshake.getPeerId());
            // Now that we know the peer ID thats coming in, we can set the null peerInfo object equal to the one we have in the peerInfo list.
            PeerInfoParser peerInfoParser = new PeerInfoParser();
            peerInfoParser.readFile();
            System.out.println(peerInfoParser.getPeerInfoList().toString());
            boolean success = false;
            for (PeerInfo peerInfo : peerInfoParser.getPeerInfoList()) {
                System.out.println("Looked at Peer ID: " + peerInfo.getPeerID() + " against " + remotePeerId);
                if (peerInfo.getPeerID() == remotePeerId) {
                    this.peerInfo = peerInfo;
                    success = true;
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
        

        // Send handshake message
        try {
            System.out.println("Sending handshake to client");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            Handshake handshakeMessage = new Handshake(origID);
            byte[] handshakeBytes = handshakeMessage.getBytes();
            out.writeObject(handshakeBytes);
            out.flush();
            System.out.println("Sent handshake to client");
        } catch (Exception e) {
            System.out.println("Error sending handshake message: " + e.getMessage());
        }
    }


        /*
        // Receive handshake message
        byte[] responseBytes = null;
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            Object obj = in.readObject();
            if (!(obj instanceof Handshake)) {
                System.out.println("Did not receive a Handshake object.");
                System.out.println("Received: " + obj.getClass().getName());
            }
            Handshake incomingHandshake = (Handshake) obj;
            responseBytes = incomingHandshake.getBytes();
            System.out.println("Got:" + incomingHandshake.getHandshakeHeader());
        } catch (Exception e) {
            System.out.println("Error receiving handshake message: " + e.getMessage());
        } finally {
            if (responseBytes == null) {
                throw new IOException("Handshake response message was null.");
            }
        }

        Handshake responseHandshake = new Handshake(responseBytes);
        System.out.println("Received handshake message from peer " + responseHandshake.getPeerId());
        // Store the remote peer ID
        setRemotePeerId(responseHandshake.getPeerId());
        // Now that we know the peer ID thats coming in, we can set the null peerInfo object equal to the one we have in the peerInfo list.
        PeerInfoParser peerInfoParser = new PeerInfoParser();
        for (PeerInfo peerInfo : peerInfoParser.getPeerInfoList()) {
            if (peerInfo.getPeerID() == remotePeerId) {
                this.peerInfo = peerInfo;
                break;
            }
        }

        // Send handshake message
        Handshake handshakeMessage = new Handshake(peerInfo.getPeerID());
        if (!handshakeMessage.getHandshakeHeader().equals(responseHandshake.getHandshakeHeader())) {
            throw new IOException("Invalid handshake header received.");
        }

        outputStream.writeObject(handshakeMessage.getBytes());
        outputStream.flush();

    }
*/    
    public void setPeerInfo(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }















}

