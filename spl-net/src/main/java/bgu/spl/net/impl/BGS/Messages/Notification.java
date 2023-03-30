package bgu.spl.net.impl.BGS.Messages;

import bgu.spl.net.impl.BGS.Messages.Message;

public class Notification extends Message {

    private Byte notificationType;
    private String postingUser;
    private String content;

    public Notification(boolean isPublic, String postingUser, String content) {
        super((short) 9, null);
        notificationType =  (byte)(isPublic ? '1' : '0');
        System.out.println("is public??   " + byteToChar(notificationType));
        this.postingUser = postingUser;
        this.content = content;
//        System.out.println("NOTIFICATION created : " + content);
    }

//    private void init() {
//        String s = getMsgString().substring(2);
//        notificationType = Byte.parseByte(s.substring(0,1));
//        s = s.substring(1);
//        int i = s.charAt(' ');
//        postingUser = s.substring(0, i);
//        content = s.substring(i+1);
//    }

    public boolean isValid() {
        return notificationType != null && postingUser != null && content != null;
    }

    @Override
    public byte[] encode() {
        byte[] opBytes = shortToBytes(getOpcode());
        byte[] nTypeBytes = new byte[]{notificationType};
        byte[] postingUserBytes = stringToBytes("X" + postingUser);
        byte[] contentBytes = stringToBytes(content);
        System.out.println("content : " + content);
        System.out.println("posting user : " + postingUser);
        return combine(new byte[][]{opBytes,nTypeBytes, postingUserBytes, zeroByte, contentBytes , zeroByte});
    }
}
