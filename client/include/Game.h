#pragma once
#include<string> 
using std::string;
#include<vector>
using std::vector;
#include "event.h"
#include<map>

class Game{ //saves data of a game to track it
    private:
    string game_name; // =topic =channel
    int subId;
    std::map<string, vector<Event>> user_events;
    
    public:
    Game(string _game_name, int _subId);
    void add_event(string user, Event event);
    vector<Event>& get_events(string user);
    int getSubId();
    string get_name();

};

