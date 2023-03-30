package bgu.spl.net.impl.BGS;

import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.srv.Reactor;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        if(args.length<2){
            System.out.println("input invalid! (port, number Of Threads)");
            System.exit(1);
        }

        Server.reactor(Integer.parseInt(args[0]), Integer.parseInt(args[1]), MessagingProtocolImpl::new, MessageEncoderDecoderImpl::new).serve();
    }
}
