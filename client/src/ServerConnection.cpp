#include "ServerConnection.h"
#include<iostream>
#include<sstream> //for the string to map func
using std::cout;
using std::endl;


ServerConnection::ServerConnection(ConnectionHandler& _ch, StompProtocol& _protocol, Client& _client) : ch(_ch), protocol(_protocol), client(_client){}

ServerConnection::~ServerConnection() = default;

void ServerConnection::run(){
    bool error = false;
    
    while(ch.connected && !error){
        string serverReply;
        if(ch.getFrameAscii(serverReply, '\0')){
            proccess(serverReply);
        }
        else{
            std::cout << "couldnt get the reply frame from the server, disconnecting...\n" << std::endl;
            error = true;
            ch.close();
        }
    }
}



void ServerConnection::proccess(string message){
    string command = message.substr(0, message.find("\n"));
    if(command == "MESSAGE"){
        message.erase(0, message.find("\n")+1);
        message.erase(0, message.find("user: ")+6);
        string username = message.substr(0, message.find("\n"));
        message.erase(0, message.find("team a")+8);
        string team_a_name = message.substr(0, message.find("\n"));
        message.erase(0,message.find("team b")+8);
        string team_b_name = message.substr(0, message.find("\n"));
        message.erase(0,message.find("event name")+12);
        string name = message.substr(0, message.find("\n"));
        message.erase(0,message.find("\ntime:")+6);
        int time = std::stoi(message.substr(0, message.find("\n")));
        message.erase(0,message.find("updates")+9);
        string game_updates_string = message.substr(0, message.find("team a updates"));
        map<string, string> game_updates = stringToMap(game_updates_string);
        message.erase(0,message.find("updates")+10);
        map<string, string> team_a_updates = stringToMap(message.substr(0, message.find("team b updates")));
        message.erase(0,message.find("updates")+9);
        map<string, string> team_b_updates = stringToMap(message.substr(0, message.find("descrip")));
        message.erase(0,message.find("descrip"));
        message.erase(0,message.find("\n")+1);
        string description = message;
        Event event(team_a_name, team_b_name, name, time, game_updates,team_a_updates, team_b_updates, description);
        string game_name = team_a_name+"_"+team_b_name;
        client.getGame(game_name).add_event(username, event);


    }
    if(command == "RECEIPT"){
        if(client.getQ().front() == "join"){
            client.getQ().pop();
            cout<< client.getQ().front() << std::endl;
            client.getQ().pop();
        }
        else if(client.getQ().front() == "exit"){
            client.getQ().pop();
            cout<< client.getQ().front() << std::endl;
            client.getQ().pop();

        }
        else if(client.getQ().front() == "logout"){
            client.getQ().pop();
            cout<< "Logged out successfully" << std::endl;
            ch.connected= false;
            ch.close();
        }
    }
    if(command == "ERROR"){
        ch.connected = false;
        ch.close();
    }
}

string ServerConnection::substring(string s, int start, int end){
    return s.substr(start, end - start);
}
map<string, string> ServerConnection::stringToMap(string s){
    map<string, string> output;
    if(!(s.empty() || s == "\n")){
        string workOnMe = s;
        if(workOnMe.at(workOnMe.length()-1) != '\n')
            workOnMe.append("\n");
        while(!workOnMe.empty() && workOnMe.find(":") != string::npos){
            if(workOnMe.at(0) == '\n')
                workOnMe.erase(0,1);
            string first = workOnMe.substr(0, workOnMe.find(":"));
            workOnMe.erase(0, first.length()+2);
            string second = workOnMe.substr(0,workOnMe.find("\n"));
            if(workOnMe.length() == second.length())
                workOnMe.clear();
            else
                workOnMe.erase(second.length()+1);
            output.insert({first, second});
        }
    }
    return output;
}