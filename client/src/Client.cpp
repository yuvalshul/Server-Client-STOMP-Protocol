#include "Client.h"
#include<string>
#include<memory>
using std::string;

int Client::subIdGenerator = 0;
int Client::recieptIdGenerator = 0;
Client::Client(string _username, string _passcode) : username(_username), passcode(_passcode), topic_game(map<string, Game>()), commands(std::queue<string>()){}
int Client::getSubId(){
    int output = subIdGenerator;
    subIdGenerator++;
    return output;
}

string& Client::getPasscode(){ return passcode; }
string& Client::getUsername(){ return username; }
bool Client::hasGame(string topic){ return topic_game.count(topic) > 0; }
Game& Client::getGame(string topic){ return topic_game.at(topic); }
void Client::unsubscribe(string topic){
    topic_game.erase(topic);
}
void Client::subscribe(string topic, int subId){
    if(topic_game.count(topic) <= 0)
        topic_game.insert({topic, Game(topic, subId)});
}
int Client::getRecieptId(){
    int output = recieptIdGenerator;
    recieptIdGenerator++;
    return output;
}
void Client::disconnect(){
    topic_game.clear();
}
std::queue<string>& Client::getQ(){ return commands; }
