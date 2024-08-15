// package bgu.spl.net.impl.echo;

// import bgu.spl.net.srv.Server;
// import bgu.spl.net.srv.ConnectionsImpl;

// public class EchoServer {

//     public static void main(String[] args) {

//         // you can use any server... 
//         // Server.threadPerClient(
//         //         7777, //port
//         //         () -> new EchoProtocol(), //protocol factory
//         //         LineMessageEncoderDecoder::new, //message encoder decoder factory
//         //         new ConnectionsImpl<>()
//         // ).serve();

//         // Server.reactor(
//         //         Runtime.getRuntime().availableProcessors(),
//         //         7777, //port
//         //         () -> new EchoProtocol<>(), //protocol factory
//         //         LineMessageEncoderDecoder::new //message encoder decoder factory
//         // ).serve();
//     }
// }
