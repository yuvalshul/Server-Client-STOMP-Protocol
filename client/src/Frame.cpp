#include "Frame.h"
#include<iostream>

Frame::Frame(string _stompCommand): stompCommand(_stompCommand+"\n"), headers(""), body(""){}
Frame::Frame() : stompCommand(""), headers(""), body(""){}
void Frame::setStompCommand(string stompCommand){
    this->stompCommand = stompCommand + "\n";

}
void Frame::setHeaders(string headers){
    this->headers = headers+"\n\n";
}
void  Frame::setBody(string body){
    this->body = body + "\n";
}
Frame Frame::sendFrame(string destination, string message, string optionalRecieptId){
    Frame frame = Frame("SEND");
    string head = ("destination:"+destination);
    if(optionalRecieptId == "")
        frame.setHeaders(head);
    else
        frame.setHeaders(head+"\nreceipt-id:"+optionalRecieptId);
    frame.setBody(message);
    return frame;
}
Frame Frame::connectFrame(string login, string passcode, string optionalRecieptId){
    Frame frame = Frame("CONNECT");
    string head = ("accept-version:1.2\nhost:stomp.cs.bgu.ac.il\nlogin:"+login+"\npasscode:"+passcode);
    if(optionalRecieptId == "")
        frame.setHeaders(head);
    else
        frame.setHeaders(head+"\nreceipt-id:"+optionalRecieptId);
    return frame;
}
Frame Frame::subscribeFrame(string destination, int subId, string optionalRecieptId){
    Frame frame = Frame("SUBSCRIBE");
    string head = ("destination:"+destination+"\nid:"+std::to_string(subId));
    if(optionalRecieptId == "")
        frame.setHeaders(head);
    else
        frame.setHeaders(head+"\nreceipt-id:"+optionalRecieptId);
    return frame;
}
Frame Frame::unsubscribeFrame(int subId, string optionalRecieptId){
    Frame frame  = Frame("UNSUBSCRIBE");
    string head = "id:";
    head.append(std::to_string(subId));
    if(optionalRecieptId == "")
        frame.setHeaders(head);
    else{
        frame.setHeaders(head+"\nreceipt-id:"+optionalRecieptId);
    }
    return frame;
}
Frame Frame::disconnectFrame(string recieptId){
    Frame frame = Frame("DISCONNECT");
    frame.setHeaders("reciept:"+recieptId);
    return frame;
}
string Frame::toString(){ 
    //return string(); }
    string output = "";
    output.append(stompCommand + headers + body + "^@" + '\u0000');
    return output;
}


