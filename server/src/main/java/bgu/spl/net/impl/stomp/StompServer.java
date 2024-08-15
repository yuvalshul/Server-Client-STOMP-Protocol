package bgu.spl.net.impl.stomp;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.Server;


public class StompServer {

    public static void main(String[] args) {
        // TODO: implement this
        Connections<String> connections = new ConnectionsImpl<>();
        if(args[1].equals("tpc")){
            Server.threadPerClient(7777,()-> new StompMessagingProtocolImpl(), ()-> new StompMessageEncoderDecoder(), connections).serve();
        }
         else if(args[1].equals("reactor")){
             Server.reactor(5, 7777, ()-> new StompMessagingProtocolImpl(), ()-> new StompMessageEncoderDecoder(), connections).serve();
         }
    }
}
