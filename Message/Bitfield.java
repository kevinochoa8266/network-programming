package Message;

import java.nio.ByteBuffer;
public class Bitfield {

public static final int MESSAGE_TYPE_BYTES = 4;
private static final byte MESSAGE_TYPE_BITFIELD = 5;
private final ByteBuffer buffer;

    public Bitfield(byte[] bitfieldData){
        if (bitfieldData == null){
            throw new IllegalArgumentException("null");
        }

        // add 1 for the message type
        int messageSize = bitfieldData.length + 1;
        buffer = ByteBuffer.allocate(messageSize + 4);
        buffer.putInt(messageSize);
        int byteSize = (int)Math.ceil(bitfieldData.length / 8.0);

        for (int i = 0; i < byteSize; i++){
            byte tempbyte = 0;
            int start = i*8;
            int end = (i+1) * 8;
            if (end > bitfieldData.length){
                end = bitfieldData.length;
            }

            for (int j = start; j < end; j++){
                int bitLocation = j % 8;
                // important variable
                int mask = 1 << (7 - start);
                if(bitfieldData[j] == 1){
                    //
                    tempbyte |= (1 << (7-(j%8)));
                }
            }
            buffer.put(tempbyte);
        }
        buffer.rewind();

    }

    public byte[] getBytes() {
        int messageLocation = buffer.position();
        byte[] message = new byte[messageLocation];
        buffer.get(message);
        return message;
    }
}
