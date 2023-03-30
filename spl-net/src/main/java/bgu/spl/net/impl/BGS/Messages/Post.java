package bgu.spl.net.impl.BGS.Messages;

import bgu.spl.net.impl.BGS.DataBase;
import bgu.spl.net.impl.BGS.Messages.Message;

import java.util.LinkedList;
import java.util.List;

public class Post extends Message {

    private String content;
    private List<String> dest;

    public Post(byte[] bytes) {
        super((short) 5, bytes);
        dest = new LinkedList<>();
        content = "";
        init();
    }

    private void init() {
        for(int i = 2; bytes[i] != zeroByte[0]; i++) {
            content += byteToChar( bytes[i]);
        }
        System.out.println(content);
        filter();
        System.out.println(content);
        for(int i = 0; i<content.length(); i++){
            if (content.charAt(i) == '@'){
                String user = "";
                i++;
                while (i<content.length() && content.charAt(i) != ' '){
                    user +=content.charAt(i);
                    i++;
                }
                System.out.println("user taged : " + user);
                dest.add(user);
            }
        }

        }
private void filter(){
    List<String> filtered = DataBase.getInstance().getFilteredWords();
    for(String f : filtered) {
        content = content.replaceAll(" " + f, "");
        content = content.replaceAll(f + " ", "");
    }
}
public List<String> getDest(){return dest;}
    public boolean isValid() {
        return content != null;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    public String getContent(){return content;}
}