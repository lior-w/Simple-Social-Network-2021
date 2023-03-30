package bgu.spl.net.impl.BGS.Messages;

import bgu.spl.net.impl.BGS.Messages.Message;

public class Error extends Message {

    private Short messageOpcode = null;

    public Error(short msgOpcode) {
        super((short) 11, null);
        messageOpcode = msgOpcode;
    }

    public boolean isValid() {
        return messageOpcode != null;
    }

    @Override
    public byte[] encode() {
        byte[] opBytes = shortToBytes(getOpcode());
        byte[] messageOpcodeBytes = shortToBytes(messageOpcode);
        return combine(new byte[][]{opBytes, messageOpcodeBytes});
    }
}
