package bgu.spl.net.impl.BGS.Messages;


import java.nio.charset.StandardCharsets;

public abstract class Message {

    protected short opcode;
    protected byte[] bytes;
    protected byte[] endByte;
    protected byte[] zeroByte;

    public Message(short op, byte[] bytes) {
        opcode = op;
     //   bytes = new byte[1 << 10]; //start with 1k;
        endByte = ";".getBytes(StandardCharsets.UTF_8);
        zeroByte = new byte[]{(byte)'\0'};
        this.bytes = bytes;
    }


    public static Message factory(byte[] bytes) {
        short opcode = bytesToShort(bytes[0],bytes[1]);
        System.out.println(opcode);
        byte zeroByte = (byte)'\0';
        byte endByte = (byte)';';
        if (opcode == 1){
            return new Register(bytes);
        }
        else if (opcode == 2){

            return new Login(bytes);
        }
        else if (opcode == 3){
            return new Logout();
        }
        else if (opcode == 4){
            return new Follow(bytes);
        }
        else if (opcode == 5){
            return new Post(bytes);
        }
        else if (opcode == 6){
            return new PM(bytes);
        }
        else if (opcode == 7){
            return new Logstat();
        }
        else if (opcode == 8){
            return new Stat(bytes);
        }
//        else if (opcode == 9) return new Notification(s);
//        else if (opcode == 10) return new Ack(s);
//        else if (opcode == 11) return new Error(s);
        else if (opcode == 12) return new Block(bytes);
        return null;
    }


    public Short getOpcode() {
        return opcode;
    }

    public abstract boolean isValid();
    public abstract byte[] encode();

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    public static short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public static short bytesToShort(byte byte1, byte byte2)
    {
        short result = (short)((byte1 & 0xff) << 8);
        result += (short)(byte2 & 0xff);
        return result;
    }

    public static char byteToChar(byte b){
        return (char)b;
    }
    public static String bytesToString(byte[] b){
        return new String(b);
    }

    public byte[] stringToBytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] combine(byte[][] bytes){
        int size = 0;
        for(byte[] b:bytes)
            size = size+b.length;
        byte[] combBytes = new byte[size+1];
        int i = 0;
        for(byte[] b : bytes){
            for(int j = 0; j<b.length; j++, i++){
                combBytes[i] = b[j];
            }
        }
        combBytes[combBytes.length-1] = endByte[0];  //add ; to the end
        return combBytes;
    }

}
