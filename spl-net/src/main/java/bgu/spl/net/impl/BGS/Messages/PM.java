package bgu.spl.net.impl.BGS.Messages;

import bgu.spl.net.impl.BGS.DataBase;
import bgu.spl.net.impl.BGS.Messages.Message;

import java.util.List;

public class PM extends Message {

    private String userName;
    private String content;
    private String sendingDataAndTime;

    public PM(byte[] bytes) {
        super((short) 6, bytes);
        userName = "";
        content = "";
        sendingDataAndTime = "";
        init();
    }
    private void init() {
        int i = 2;
        while(bytes[i]!=zeroByte[0]){
            userName +=byteToChar(bytes[i]);
            i++;
        }
        i++;

        while(bytes[i]!=zeroByte[0]){
            content +=byteToChar(bytes[i]);
            i++;
        }
        i++;
        while(bytes[i]!=zeroByte[0]){
            sendingDataAndTime +=byteToChar(bytes[i]);
            i++;
        }
        filter();
    }

    private void filter(){
        List<String> filtered = DataBase.getInstance().getFilteredWords();
        for(String f : filtered) {
            content = content.replaceAll(" " + f, "");
            content = content.replaceAll(f + " ", "");
        }
    }
    public String getUser(){return userName;}

    public String getContent(){return content;}

	public String getDate(){return sendingDataAndTime;}

    public boolean isValid() {
        return userName != null && content != null && sendingDataAndTime != null;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

}
