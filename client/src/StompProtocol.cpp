#include "StompProtocol.h"
#include<string>
#include "Frame.h"
#include "event.h"
#include<iostream>
#include<fstream>
#include "ServerConnection.h"
#include<memory>

using std::cout;
using std::endl;




StompProtocol::StompProtocol(Client& _client, ConnectionHandler& _ch) : client(_client), ch(_ch) {}

void StompProtocol::loginCommand(){
    Frame connect = Frame::connectFrame(this->client.getUsername(),this->client.getPasscode(), std::to_string(client.getRecieptId()));

}
void StompProtocol::joinGameChannelCommand(string game_name){
    client.getQ().push("Joined channel "+game_name);
    int subId = client.getSubId();
    string subscribe = Frame::subscribeFrame(game_name, subId, std::to_string(client.getRecieptId())).toString();
    ch.sendLine(subscribe);
    client.subscribe(game_name, subId);
}
void StompProtocol::exitGameChannelCommand(string game_name){
    client.getQ().push("Exited channel "+game_name);
    string unsubscribe = Frame::unsubscribeFrame(client.getGame(game_name).getSubId(), std::to_string(client.getRecieptId())).toString();
    ch.sendLine(unsubscribe);
    client.unsubscribe(game_name);
}
void StompProtocol::logOutCommand(){
    string disconnect = Frame::disconnectFrame(std::to_string(client.getRecieptId())).toString();
    ch.sendLine(disconnect);
}
void  StompProtocol::reportToChannelCommand(string file){
    names_and_events names_and_events = parseEventsFile(file);
    vector<Event> events = names_and_events.events;
    string team_a_name  = names_and_events.team_a_name;
    string team_b_name = names_and_events.team_b_name;
    string game_name = team_a_name+"_"+team_b_name;
    if(!events.empty()){
        if(client.hasGame(game_name)){
            for(Event event : events){
                string message ="user: "+client.getUsername()+"\nteam a: "+event.get_team_a_name()+"\nteam b: "+event.get_team_b_name()+
                "\nevent name: "+event.get_name()+"\ntime: "+std::to_string(event.get_time())+"\ngeneral updates:\n";
                map<string, string> game_updates = event.get_game_updates();
                for(std::pair<string, string> game_update : game_updates)
                    message.append(game_update.first + ": " +game_update.second+"\n");
                message.append("\nteam a updates: \n");
                map<string, string> team_a_updates = event.get_team_a_updates();
                for(std::pair<string, string> team_a_update : team_a_updates)
                    message.append(team_a_update.first + ": " +team_a_update.second+"\n");
                message.append("team b updates: \n");
                map<string, string> team_b_updates = event.get_team_b_updates();
                for(std::pair<string, string> team_b_update : team_b_updates)
                    message.append(team_b_update.first + ": " +team_b_update.second+"\n");
                message.append("\ndescription: \n"+event.get_discription());
                string send = Frame::sendFrame(game_name, message, std::to_string(client.getRecieptId())).toString();
                ch.sendLine(send);
            }
            
        }
    }
}
void StompProtocol::summarizeGameCommand(string game_name, string user, string file){
    string summary;
    Game game = client.getGame(game_name);
    vector<Event> eventsByUser = game.get_events(user);
    if(!eventsByUser.empty()){
        string team_a_name = eventsByUser.at(0).get_team_a_name();
        string team_b_name = eventsByUser.at(0).get_team_b_name();

        summary.append(team_a_name+ " vs "+team_b_name+"\n");
        summary.append("Game stats:\n");
        summary.append("General stats:\n");
        for(unsigned int i = 0; i < eventsByUser.size(); i++){
            Event event = eventsByUser.at(i);
            const map<string, string> game_updates = event.get_game_updates();
            for(std::pair<string, string> stat : game_updates){
                summary.append(stat.first + ": " + stat.second + "\n");
            }
        }
        summary.append("\n");
        summary.append(team_a_name + " stats:\n");
        for(unsigned int i = 0; i < eventsByUser.size(); i++){
            Event event = eventsByUser.at(i);
            const map<string, string> team_a_updates = event.get_team_a_updates();
            for(std::pair<string, string> stat : team_a_updates){
                    summary.append(stat.first + ":" + stat.second + "\n");
            }
        }

        summary.append("\n");
        summary.append(team_b_name + " stats:\n");
        for(unsigned int i = 0; i < eventsByUser.size(); i++){
            Event event = eventsByUser.at(i);
            const map<string, string> team_b_updates = event.get_team_b_updates();
            for(std::pair<string, string> stat : team_b_updates){
                    summary.append(stat.first + ":" + stat.second + "\n");
            }
        }

        summary.append("\n");    
        summary.append("Game events reports:\n");
        for(unsigned int i = 0; i < eventsByUser.size(); i++){
            Event event = eventsByUser.at(i);
            summary.append(std::to_string(event.get_time()) +" - " +event.get_name() +"\n\n");
            string desc = event.get_discription();
            if(((desc.length()>=2 && ((desc.at(desc.length()-1) == '^') && ((desc.at(desc.length()-2) == '@'))))));
            desc = desc.substr(0,desc.length()-2);
            summary.append(desc+ "\n\n\n");
        }

    }
    std::ofstream outfile;
    outfile.open(file, std::ios::out);
    outfile << summary;
}



