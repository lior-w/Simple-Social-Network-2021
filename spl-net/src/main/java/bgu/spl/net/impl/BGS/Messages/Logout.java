package bgu.spl.net.impl.BGS.Messages;

public class Logout extends Message {

    public Logout() {
        super((short) 3, null);
    }

    public boolean isValid() {
        return true;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

}
