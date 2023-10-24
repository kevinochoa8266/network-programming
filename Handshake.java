import java.nio.ByteBuffer;

public class Handshake {

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

    public byte[] getBytes() {
        byte[] message = new byte[HANDSHAKE_HEADER_LENGTH + ZERO_BITS_LENGTH + PEER_ID_LENGTH];
        buffer.get(message);
        return message;
    }

}
