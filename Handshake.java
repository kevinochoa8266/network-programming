import java.io.Serializable;
import java.nio.ByteBuffer;

public class Handshake implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final int HANDSHAKE_HEADER_LENGTH = 18;
    private static final int ZERO_BITS_LENGTH = 10;
    private static final int PEER_ID_LENGTH = 4;
    private static final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";

    private final ByteBuffer buffer;

    public Handshake(int peerId) {
        buffer = ByteBuffer.allocate(HANDSHAKE_HEADER_LENGTH + ZERO_BITS_LENGTH + PEER_ID_LENGTH);

        // Set the handshake header
        buffer.put(HANDSHAKE_HEADER.getBytes());

        // Fill zero bits
        buffer.put(new byte[ZERO_BITS_LENGTH]);

        // Set the peer ID
        buffer.putInt(peerId);

        buffer.rewind();
    }

    public Handshake(byte[] receivedBytes){
        buffer = ByteBuffer.wrap(receivedBytes);
    }

    public byte[] getBytes() {
        byte[] message = new byte[HANDSHAKE_HEADER_LENGTH + ZERO_BITS_LENGTH + PEER_ID_LENGTH];
        buffer.rewind();
        buffer.get(message);
        return message;
    }

    public String getHandshakeHeader(){
        byte[] headerBytes = new byte[HANDSHAKE_HEADER_LENGTH];
        buffer.position(0);
        buffer.get(headerBytes);
        return new String(headerBytes);
    }

    public int getPeerId(){
        buffer.position(HANDSHAKE_HEADER_LENGTH + ZERO_BITS_LENGTH);
        return buffer.getInt();
    }


}
