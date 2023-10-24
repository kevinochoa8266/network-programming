package Message;

import java.nio.ByteBuffer;

public class Interested {

    private static final int MESSAGE_LENGTH_BYTES = 4;
    private static final byte MESSAGE_TYPE_INTERESTED = 2;

    private final ByteBuffer buffer;

    public Interested() {
        buffer = ByteBuffer.allocate(MESSAGE_LENGTH_BYTES + 1);
        buffer.putInt(0);
        buffer.put(MESSAGE_TYPE_INTERESTED);
        buffer.rewind();
    }

    public byte[] getBytes() {
        byte[] message = new byte[5];
        buffer.get(message);
        return message;
    }

}
