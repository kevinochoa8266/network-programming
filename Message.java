import java.io.Serializable;

public class Message implements Serializable {
    private final int length; // Length of the message payload
    private final byte type; // Message type field
    private final byte[] payload; // Message payload

    public Message(int length, int i, byte[] payload) {
        this.length = length;
        this.type = (byte) i;
        this.payload = payload;
    }

    public int getLength() {
        return length;
    }

    public byte getType() {
        return type;
    }

    public byte[] getPayload() {
        return payload;
    }
}