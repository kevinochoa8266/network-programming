package Message;

import java.nio.ByteBuffer;

public class Have {

    private static final int MESSAGE_LENGTH_BYTES = 4;
    private static final byte MESSAGE_TYPE_HAVE = 4;
    private static final byte MESSAGE_PAYLOAD = 4;

    private final ByteBuffer buffer;

    public Have(int index) {
        buffer = ByteBuffer.allocate(MESSAGE_LENGTH_BYTES + 1 + MESSAGE_PAYLOAD);
        buffer.putInt(MESSAGE_LENGTH_BYTES);  // Message length (excluding the type)
        buffer.put(MESSAGE_TYPE_HAVE);         // Message type
        buffer.putInt(index);                 // Payload: The piece index
        buffer.rewind();
    }

    public byte[] getBytes() {
        byte[] message = new byte[MESSAGE_LENGTH_BYTES + 1 + MESSAGE_PAYLOAD];
        buffer.get(message);
        return message;
    }

}

