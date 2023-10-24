import java.net.Socket;

public class Peer {
    private int myPeerId;
    private int peerId;
    private Socket socket;
    private byte[] bitfield;
    private boolean interested;

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

}

