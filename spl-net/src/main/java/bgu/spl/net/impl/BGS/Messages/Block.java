package bgu.spl.net.impl.BGS.Messages;

import bgu.spl.net.impl.BGS.Messages.Message;

public class Block extends Message {

    private String userName;

    public Block(byte[] bytes) {
        super((short) 12, bytes);
        userName = "";
        init();
    }

    private void init() {
        for(int i = 2; bytes[i] != zeroByte[0]; i++){
            userName+=byteToChar(bytes[i]);
        }
        System.out.println("BLOCK _" +userName + "_");
    }
    @Override
    public byte[] encode() {
//        byte[] opBytes = shortToBytes(getOpcode());
//        byte[] userNameBytes = stringToBytes(userName);
//        return combine(new byte[][]{opBytes, userNameBytes, zeroByte});
        return new byte[0];
    }
    public boolean isValid() {
        return userName != null;
    }

    public String getUserName() {
        return userName;
    }

}
