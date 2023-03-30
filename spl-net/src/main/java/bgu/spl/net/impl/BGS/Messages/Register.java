package bgu.spl.net.impl.BGS.Messages;

import bgu.spl.net.impl.BGS.Messages.Message;

public class Register extends Message {

    private String userName;
    private String password;
    private String birthday;

    public Register(byte[] bytes) {
        super((short) 1, bytes);
        userName = "";
        password = "";
        birthday = "";
        this.bytes = bytes;
        init();
        System.out.println("register: " + userName + " " +password + " " +birthday);
    }

    private void init() {
        int i = 2;
        while(bytes[i]!=zeroByte[0]){
            userName +=byteToChar(bytes[i]);
            i++;
        }
        i++;
        while(bytes[i]!=zeroByte[0]){
            password +=byteToChar(bytes[i]);
            i++;
        }
        i++;
        while(bytes[i]!=zeroByte[0]){
            birthday +=byteToChar(bytes[i]);
            i++;
        }

    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }

    public boolean isValid() {
        return userName != null && password != null && birthday != null;
    }
    @Override
    public byte[] encode() {
        return new byte[0];
    }

}
