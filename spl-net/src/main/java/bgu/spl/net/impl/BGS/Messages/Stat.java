package bgu.spl.net.impl.BGS.Messages;

import bgu.spl.net.impl.BGS.Messages.Message;

import java.util.List;

public class Stat extends Message {

    private String[] listOfUsernames;

    public Stat(byte[] bytes) {
        super((short) 8, bytes);
        init();
    }

    private void init() {
        int i = 2;
        int size = 0;
        while(bytes[i]!=zeroByte[0]){
            if(byteToChar(bytes[i])=='|')
                size++;
            i++;
        }
        listOfUsernames = new String[size];
        i = 2;
        String user="";
        while(size>0){
            char c = byteToChar(bytes[i]);
            if(c=='|') {
                listOfUsernames[(listOfUsernames.length - size)] = user;
                System.out.println("STAT " + user);
                size--;
                user = "";
            }
            else{
                user+=c;
            }
            i++;
        }
    }

    public boolean isValid() {
        return listOfUsernames != null;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    public String[] getListOfUsernames() {
        return listOfUsernames;
    }
}