#include "Game.h"
#include<map>
using std::map;

Game::Game(string _game_name, int _subId): game_name(_game_name), subId(_subId), user_events(map<string, vector<Event>>()) {}

int Game::getSubId(){ return subId; }

string Game::get_name(){ return game_name; }

void Game::add_event(string user, Event event){
    if(user_events.count(user) <= 0)
        user_events[user] = vector<Event>();
    user_events[user].push_back(event);
}

vector<Event>& Game::get_events(string user){ return user_events[user]; }

    