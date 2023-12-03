package Message;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class Piece {
    private static final int MESSAGE_TYPE_BYTES = 7;

    private final ByteBuffer buffer;

    public Piece(int location, int offset, byte[] data){
        //
        int messageLength = data.length + 9;

        buffer = ByteBuffer.allocate(messageLength);
        buffer.putInt(messageLength);
        buffer.put((byte) MESSAGE_TYPE_BYTES);
        buffer.putInt(location);
        buffer.putInt(offset);
        buffer.put(data);
        //

        buffer.rewind();


    }



    public byte[] getBytes(){
        int tempMessageLength = buffer.capacity();
        byte[] message = new byte[tempMessageLength];
        buffer.rewind();
        buffer.get(message);
        return message;
    }

    public int getMessageLength(){
        buffer.rewind();
        return buffer.getInt();
    }

    public int getPieceIndex(){
        buffer.position(5);
        return buffer.getInt();
    }

    public byte[] getPieceData(){
        buffer.position(9);
        byte[] data = new byte[buffer.capacity() - 9];
        buffer.get(data);
        return data;
    }


}
