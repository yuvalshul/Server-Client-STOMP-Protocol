#pragma once
#include<string>
using std::string;
#include<map>
using std::map;
#include "Game.h"
#include "ConnectionHandler.h"
#include<queue>

class Client{
private:
string username;
string passcode;
map<string, Game> topic_game; 
static int subIdGenerator;
static int recieptIdGenerator;
std::queue<string> commands;


public:
Client(string _username, string _passcode);
int getSubId();
int getRecieptId();
string& getUsername();
string& getPasscode();
Game& getGame(string topic);
void unsubscribe(string topic);
void subscribe(string topic, int subId);
void disconnect();
std::queue<string>& getQ();
bool hasGame(string topic);
};

