#include "ConnectionHandler.h"
#include "event.h"
#include "Client.h"
#include "Frame.h"
#include "ServerConnection.h"
#include<thread>
#include <iostream>
#include <string>
using std::cout;
using std::cin;
using std::string;
#include "StompProtocol.h"
using std::endl;
/*
login 127.0.0.1:7777 meni films
summary a_b meni ./bin/summaryTest.o
report ./data/events1.json
login 127.0.0.1:7777 yuval abc
summary a_b yuval ./bin/summaryTest.o
*/
//stomp client main
int main (int argc, char *argv[]) { 
	string login, hostAndPort, host, username, passcode;
    short port;

	cin >> login, cin >> hostAndPort, cin >> username, cin >> passcode;
   
    host = hostAndPort.substr(0, hostAndPort.find(":"));
    port = std::stoi(hostAndPort.substr(hostAndPort.find(":")+1, hostAndPort.find(" ")));
    if(username.at(username.length()-1) == ' '){
        username = username.erase(username.length()-1, 1);
    }
    if(passcode.at(passcode.length()-1) == ' '){
        passcode = passcode.erase(passcode.length()-1, 1);
    }
	//create connectionHandler and try to connect to the server
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to server" << std::endl;
        return 1;
    }
	Client client(username, passcode);
	StompProtocol protocol(client, connectionHandler);
    string connectFrame = Frame::connectFrame(username, passcode, "").toString();
	if(!connectionHandler.sendLine(connectFrame)){
        cout<< "couldn't send the message, disconnecting..."<<std::endl;
        return 1;    
    }
    string serverReply;
    if(!connectionHandler.getLine(serverReply)){
        std::cerr << "Cannot connect to server" << std::endl;
        return 1;
    }
    else{
        if(serverReply.substr(0,serverReply.find("\n")) == "CONNECTED"){
            connectionHandler.connected = true;
            cout << "Login successful" << std::endl;
        }
        else{
            return 1;
        }
    }
    //create the thread that will deal with the server
    ServerConnection serverConnection(connectionHandler, protocol, client);
    std::thread serverConnectionThread(&ServerConnection::run, &serverConnection);
    
    //the main thread will run this loop which deals with the keyboard
    while (connectionHandler.connected) {
        char buf[1024];
        string keyBoardInput;
        cin.getline(buf,1024);
        keyBoardInput = buf;
        if(keyBoardInput.length() > 0){
            string frame;
            if(keyBoardInput.at(0) == ' ')
                keyBoardInput.erase(0,1);
            string command;
            if(keyBoardInput!= "logout"){
                command = keyBoardInput.substr(0, keyBoardInput.find(" "));
                keyBoardInput.erase(0, command.length()+1);
            }
            else
                command = keyBoardInput;
            if(command == "join"){
                client.getQ().push("join");
                protocol.joinGameChannelCommand(keyBoardInput);
            }
            else if(command == "exit"){
                client.getQ().push("exit");
                protocol.exitGameChannelCommand(keyBoardInput);
            }
            else if(command == "logout"){
                client.getQ().push("logout");
                protocol.logOutCommand();
                // connectionHandler.connected = false;
            }
            else if(command == "report"){
                protocol.reportToChannelCommand(keyBoardInput);
            }
            else if(command == "summary"){
                string game_name = keyBoardInput.substr(0, keyBoardInput.find(" "));
                keyBoardInput.erase(0, game_name.length()+1);
                string user = keyBoardInput.substr(0, keyBoardInput.find(" "));
                keyBoardInput.erase(0, user.length()+1); // =file
                protocol.summarizeGameCommand(game_name, user, keyBoardInput);
            }
        }
    }
    serverConnectionThread.join();
    return 0;
}