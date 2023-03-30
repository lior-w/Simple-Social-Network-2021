package bgu.spl.net.impl.BGS.Messages;

import bgu.spl.net.impl.BGS.Messages.Message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Ack extends Message {

    private Short messageOpcode;
    private String userName;
    private short[] stat;

    public Ack(short msgOpcode, String option) {
        super((short) 10, null);
        messageOpcode = msgOpcode;
        userName = option;
        stat = new short[4];
    }


    public Ack(short msgOpcode, short[] stat) {
        super((short) 10, null);
        messageOpcode = msgOpcode;
        Queue<byte[]> bQueue = new LinkedList<>();
        for (short s: stat) {
            bQueue.add(shortToBytes(s));
        }
        this.stat = stat;
        userName = "";
    }

    public Ack(short msgOpcode) {
        super((short) 10, null);
        messageOpcode = msgOpcode;
        userName = "";
        stat = new short[4];
    }

    public boolean isValid() {
        return messageOpcode != null;
    }

    @Override
    public byte[] encode() {
        byte[] opBytes = shortToBytes(getOpcode());
        byte[] messageOpcodeBytes = shortToBytes(messageOpcode);
        if(messageOpcode == 1 || messageOpcode == 2 || messageOpcode == 3 || messageOpcode == 5
                || messageOpcode == 6 || messageOpcode == 12)
            return combine(new byte[][]{opBytes, messageOpcodeBytes});

        if (messageOpcode ==7 || messageOpcode ==8 ){
            byte[] age = shortToBytes(stat[0]);
            byte[] numPosts = shortToBytes(stat[1]);
            byte[] numFollowers = shortToBytes(stat[2]);
            byte[] numFollowing = shortToBytes(stat[3]);
            return combine(new byte[][]{opBytes, messageOpcodeBytes, age,
                    numPosts, numFollowers, numFollowing});
        }
        else{ //op = 4
            byte[] userNameBytes = stringToBytes(userName);
            return combine(new byte[][]{opBytes, messageOpcodeBytes, userNameBytes});
        }
    }


    private String byteQueueToString(Queue<byte[]> bQueue) {
        String s = "";
        while (!bQueue.isEmpty()) {
            byte[] b = bQueue.poll();
            for (byte value : b) {
                s += value;
            }
        }
        return s;
    }
}
