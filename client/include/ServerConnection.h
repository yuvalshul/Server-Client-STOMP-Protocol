#include<string>
#include<thread>
#include<ConnectionHandler.h>
#include<StompProtocol.h>

class ServerConnection
{
private:
    ConnectionHandler& ch;
    StompProtocol& protocol; //why do we need this? isnt this for the keyboard input?
    Client& client;


public:
    ServerConnection(ConnectionHandler& _ch, StompProtocol& _protocol, Client& _client);
    ~ServerConnection();
    void run();
    void proccess(string message);
    string substring(string s, int start, int end);
    static void close();
    map<string, string> stringToMap(string s);
    Client getClient();
};

