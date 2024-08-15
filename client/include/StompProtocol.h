#pragma once
#include<string>
using std::string;
#include "ConnectionHandler.h"
#include "Client.h"

// TODO: implement the STOMP protocol
class StompProtocol
{
private:
Client& client;
ConnectionHandler& ch;

public:
StompProtocol(Client& _client, ConnectionHandler& _ch);
int getSubCounter();
void loginCommand();
void joinGameChannelCommand(string game_name);
void exitGameChannelCommand(string game_name);
void logOutCommand();
void reportToChannelCommand(string file);
void summarizeGameCommand(string game_name, string user, string file);
Client getClient();
};
