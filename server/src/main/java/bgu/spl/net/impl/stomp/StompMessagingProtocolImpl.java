package bgu.spl.net.impl.stomp;
import java.util.ArrayList;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.api.StompMessagingProtocol;

public class StompMessagingProtocolImpl implements StompMessagingProtocol<String>{

    //this class is uniqe per client - every client has an instance of this class in his unique connection handler

    private volatile boolean shouldTerminate = false;
    private boolean connected = false;
    private int connectionId;
    private Connections<String> connections;

    
    public void start(int connectionId, Connections<String> connections)  {
        // TODO Auto-generated method stub
        this.connections = connections;
        this.connectionId = connectionId;  
    }

    
    public void process(String message) { //checks what is the type of frame of the messege and reply accordingly
        // TODO Auto-generated method stub
        if(message.charAt(0) =='\n')
            message = message.substring(1);
    
        String frameCommand = message.substring(0,message.indexOf('\n'));
        String frameBody = message.substring(message.indexOf('\n')+1);

        if(frameCommand.equals("SEND")){
            pSEND(frameBody);
        }
        if(frameCommand.equals("SUBSCRIBE"))
            pSUBSCRIBE(frameBody);
        if(frameCommand.equals("UNSUBSCRIBE"))
            pUNSUBSCRIBE(frameBody);
        if(frameCommand.equals("DISCONNECT")){
            pDISCONNECT(frameBody);
        }
        if(frameCommand.equals("CONNECT"))
            pCONNECT(frameBody);
        


    }

    @Override
    public boolean shouldTerminate() { return shouldTerminate; }


    private void pSEND(String frameBody){
        if(connected){
            String topic = frameBody.substring(frameBody.indexOf(':')+1, frameBody.indexOf('\n'));
            String username = frameBody.substring(frameBody.indexOf("user")+6, frameBody.indexOf("team a")-1);
            String msg = frameBody.substring(frameBody.indexOf('\n', frameBody.indexOf(username))+1);
            if(connections.isSubscribed(connectionId, topic)){
                Frame message = Frame.messageFrame(topic, connections.getSubIdByTopic(topic, username), connections.getMessageId());
                message.setBody("user: "+username+"\n"+msg);
                String messageAsString = message.toString();
                ArrayList<Integer> clientsId = new ArrayList<Integer>(connections.getClientsByTopic(topic));
                int index = messageAsString.indexOf("^@");
                messageAsString = messageAsString.substring(0, index) + messageAsString.substring(index+3);
                if(clientsId != null && !clientsId.isEmpty()){
                    for(Integer client : clientsId){
                        
                        connections.send(client, messageAsString);
                    }
                }
                if(frameBody.contains("receipt-id")){
                    String temp = frameBody.substring(frameBody.indexOf("receipt-id"));
                    String receiptId = temp.substring(temp.indexOf(":")+1, temp.indexOf("\n"));
                    String receipt = Frame.receiptFrame(receiptId).toString();
                    connections.send(connectionId, receipt);
                }
            }
            else{
                Frame error = Frame.errorFrame("","could not send the message", "SEND\n"+frameBody, "You are not subscribe to "+topic+"\nconnection is closed");
                shouldTerminate = true;
                connections.disconnect(connectionId);
                connections.send(connectionId, error.toString());
            }
        }
        else{ //not even connected yet
            Frame reply = Frame.notConnectedError();
            reply.setBody("The Message:\n-----\n"+frameBody+"\n-----\n");
            shouldTerminate = true;
            connections.send(connectionId, reply.toString());
        }
    }


    private void pSUBSCRIBE(String frameBody){
        if(connected){
            String topic = frameBody.substring(frameBody.indexOf(':')+1, frameBody.indexOf('\n'));
            String id = frameBody.substring(frameBody.indexOf("id")+3, frameBody.indexOf('\n', frameBody.indexOf("id")));
            if(connections.newSubId(id, connectionId)){
                if(!connections.isSubscribed(connectionId, topic))
                    connections.subscribe(id, topic, connectionId);
                    if(frameBody.contains("receipt-id")){
                        String temp = frameBody.substring(frameBody.indexOf("receipt-id"));
                        String receiptId = temp.substring(temp.indexOf(":")+1, temp.indexOf("\n"));
                        String receipt = Frame.receiptFrame(receiptId).toString();
                        connections.send(connectionId, receipt);
                    }
                else{
                    Frame error = Frame.errorFrame("","cannot subscribe to "+topic, "SUBSCRIBE\n"+frameBody, "You are already subscribe to "+topic+"\nconnection is closed");
                    shouldTerminate = true;
                    connections.disconnect(connectionId);
                    connections.send(connectionId, error.toString());
                }
            }
            else{
                Frame error = Frame.errorFrame("","cannot subscribe to "+topic, "SUBSCRIBE\n"+frameBody, "Your subscription id:"+id+" is already used\nconnection is closed");
                shouldTerminate = true;
                connections.disconnect(connectionId);
                connections.send(connectionId, error.toString());
            }
        }
        else{ //not even connected yet
            Frame reply = Frame.notConnectedError();
            reply.setBody("The Message:\n-----\n"+frameBody+"\n-----\n");
            shouldTerminate = true;
            connections.send(connectionId, reply.toString());
        }
    }

    private void pUNSUBSCRIBE(String frameBody) {
        if(connected){
            String id = frameBody.substring(frameBody.indexOf(':')+1, frameBody.indexOf('\n'));
            if(connections.unsubscribe(Integer.parseInt(id), connectionId)){
                if(frameBody.contains("receipt-id")){
                    String temp = frameBody.substring(frameBody.indexOf("receipt-id"));
                    String receiptId = temp.substring(temp.indexOf(":")+1, temp.indexOf("\n"));
                    String receipt = Frame.receiptFrame(receiptId).toString();
                    connections.send(connectionId, receipt);
                }
            }
        }
        else{ //not even connected yet
            Frame reply = Frame.notConnectedError();
            reply.setBody("The Message:\n-----\n"+frameBody+"\n-----\n");
            shouldTerminate = true;
            connections.send(connectionId, reply.toString());
        }

    }


    private void pCONNECT(String frameBody){
        Frame reply;
        String username = frameBody.substring(frameBody.indexOf("login")+6, frameBody.indexOf('\n', frameBody.indexOf("login")));
        String passcode = frameBody.substring(frameBody.indexOf("passcode")+9, frameBody.indexOf('\n', frameBody.indexOf("passcode")));
        int instance = connections.connect(passcode, username, connectionId);
        if(instance == 0){ //connected succefully
            connected = true;
            reply = Frame.connectedFrame();
        }
        else{
             if(instance == 1){ //incorrect passcode
                reply = Frame.errorFrame("","passcode is incorrect" ,"CONNECT\n"+frameBody ,"" );
             }
            else{ //user is already connected
                reply = Frame.errorFrame("","user is already connected" ,"CONNECT\n"+frameBody ,"" );
                connections.disconnect(connectionId);  
            }
            shouldTerminate = true;
        }
        connections.send(connectionId, reply.toString());
    }

    private void pDISCONNECT(String frameBody){
        if(connected){
            String id = frameBody.substring(frameBody.indexOf(':')+1, frameBody.indexOf('\n'));
            Frame disco = Frame.receiptFrame(id);
            connections.send(connectionId, disco.toString());
            shouldTerminate = true;
            try{
                Thread.sleep(500);
            }catch(InterruptedException e){}
            connections.disconnect(connectionId);
        }
        else{ //not even connected yet
            Frame reply = Frame.notConnectedError();
            reply.setBody("The Message:\n-----\n"+frameBody+"\n-----\n");
            connections.send(connectionId, reply.toString());
        }
        shouldTerminate = true;
    }
}
