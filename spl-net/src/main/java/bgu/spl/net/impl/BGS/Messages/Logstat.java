package bgu.spl.net.impl.BGS.Messages;

import bgu.spl.net.impl.BGS.Messages.Message;

public class Logstat extends Message {

    public Logstat() {
        super((short) 7, null);
    }

    public boolean isValid() {
        return true;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }
}
