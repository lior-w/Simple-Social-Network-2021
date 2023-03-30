package bgu.spl.net.impl.BGS;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGS.Messages.Message;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private byte[] bytes;
    private int len;

    public MessageEncoderDecoderImpl() {
        bytes = new byte[1 << 10]; //start with 1k;
        len = 0;
    }

    public Message decodeNextByte(byte nextByte) {
        //System.out.println((char)nextByte);

        pushByte(nextByte);
        if (nextByte == ';') {
            byte[] b2 = new byte[bytes.length];
            for(int i = 0; i<bytes.length; i++)
                b2[i] = bytes[i];
            bytes = new byte[1 << 10];
            len = 0;
            return Message.factory(b2);
        }
        return null;
    }

    public byte[] encode(Message message) {
        return message.encode();
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

//    private String popString() {
//        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
//        len = 0;
//        return result;
//    }
}
