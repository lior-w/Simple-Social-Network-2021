package bgu.spl.net.impl.BGS.Messages;

public class Login extends Message {

    private String userName;
    private String password;
    private Byte captcha;

    public Login(byte[] bytes) {
        super((short) 2, bytes);
        userName = "";
        password = "";
        this.bytes = bytes;
        init();
        System.out.println("log in: " + userName + " " +password);
    }

    private void init() {
        int i = 2;
        while(bytes[i]!=zeroByte[0]){
            userName +=byteToChar(bytes[i]);
            i++;
        }
        i++;
        while(bytes[i]!=zeroByte[0]){
            password += byteToChar(bytes[i]);
            i++;
        }
        i++;
        captcha = bytes[i];
        System.out.println("captcha: " + captcha);

    }

    public boolean isValid() {
        return userName != null && password != null && captcha != null;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Byte getCaptcha() {
        return captcha;
    }
}
