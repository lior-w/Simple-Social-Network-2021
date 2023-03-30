package bgu.spl.net.impl.BGS.Messages;

import bgu.spl.net.impl.BGS.Messages.Message;

public class Follow extends Message {

    private Byte follow = null;
    private String userName = null;

    public Follow(byte[] bytes) {
        super((short) 4, bytes);
        userName = "";
        this.bytes = bytes;
        init();
    }

    private void init() {
        follow = bytes[2];
        for(int i = 3; bytes[i] != endByte[0]; i++){
            userName+=byteToChar(bytes[i]);
        }
    }

    public boolean isValid() {
        return follow != null && userName != null;
    }

    public Byte getFollow() {
        return follow;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }
}
