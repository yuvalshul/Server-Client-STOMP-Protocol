package bgu.spl.net.impl.stomp;

public class Frame {

    private String stompCommand;
    private String headers;
    private String body;

    
    public Frame(String stompCommand){
        this.stompCommand = stompCommand+"\n";
    }

    public Frame(){
        this.stompCommand = "";
    }

    public void setStompCommand(String stompCommand){
        this.stompCommand = stompCommand+"\n";
    }

    public void setHeaders(String headers){
        this.headers = headers+"\n\n";
    }

    public void setBody(String body){
        if(this.body == null)
            this.body = body+"\n";
        else{
            this.body += body+"\n";
        }
    }

    public static Frame messageFrame(String destination, int subscription, int messageId){
        Frame frame = new Frame("MESSAGE");
        frame.setHeaders("destination:"+destination+"\nsubscription:"+subscription+"\nmessage-id:"+messageId);
        return frame;
    }

    public static Frame connectedFrame(){
        Frame frame = new Frame("CONNECTED");
        frame.setHeaders("version:1.2");
        return frame;
    }

    public static Frame errorFrame(String frameCaused, String message, String theMessege, String detailedInfo){
        Frame frame = new Frame("ERROR");
        if(frameCaused == "")
            frame.setHeaders("message:"+message+2*'\n'+"The messege:\n----\n"+theMessege+"\n----\n"+detailedInfo);
        else
            frame.setHeaders(frameCaused+"\nmessage:"+message+2*'\n'+"The messege:\n----\n"+theMessege+"\n----\n"+detailedInfo);
        return frame;
    }

    public static Frame receiptFrame(String id){
        Frame frame = new Frame("RECEIPT");
        frame.setHeaders("receipt-id:"+id);
        return frame;
    }

    public static Frame notConnectedError(){
        Frame frame = new Frame("ERROR");
        frame.setHeaders("messege: user not connected");
        return frame;
    }

    public String toString(){
        if(body != null)
            return stompCommand+headers+body+"^@\u0000";
        else
        return stompCommand+headers+"^@\u0000";
    }
}
