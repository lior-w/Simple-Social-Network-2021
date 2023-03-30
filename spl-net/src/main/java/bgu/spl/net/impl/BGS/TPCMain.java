package bgu.spl.net.impl.BGS;

import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.srv.Reactor;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        if(args.length<1){
            System.out.println("input invalid! ");
            System.exit(1);
        }

        Server.threadPerClient(Integer.parseInt(args[0]), MessagingProtocolImpl::new, MessageEncoderDecoderImpl::new).serve();
    }
}
