CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lpthread

all: StompWCIClient

EchoClient: bin/ConnectionHandler.o bin/echoClient.o bin/event.o bin/Client.o bin/Frame.o bin/ServerConnection.o bin/Game.o bin/StompProtocol.o
	g++ -o bin/EchoClient bin/ConnectionHandler.o bin/echoClient.o bin/event.o bin/Client.o bin/Frame.o bin/ServerConnection.o bin/Game.o bin/StompProtocol.o $(LDFLAGS)

StompWCIClient: bin/ConnectionHandler.o bin/StompClient.o bin/event.o bin/Client.o bin/Frame.o bin/ServerConnection.o bin/Game.o bin/StompProtocol.o
	g++ -o bin/StompWCIClient bin/ConnectionHandler.o bin/StompClient.o bin/event.o bin/Client.o bin/Frame.o bin/ServerConnection.o bin/Game.o bin/StompProtocol.o $(LDFLAGS)

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/event.o: src/event.cpp
	g++ $(CFLAGS) -o bin/event.o src/event.cpp

bin/StompClient.o: src/StompClient.cpp
	g++ $(CFLAGS) -o bin/StompClient.o src/StompClient.cpp

bin/Client.o: src/Client.cpp
	g++ $(CFLAGS) -o bin/Client.o src/Client.cpp

bin/Frame.o: src/Frame.cpp
	g++ $(CFLAGS) -o bin/Frame.o src/Frame.cpp

bin/ServerConnection.o: src/ServerConnection.cpp
	g++ $(CFLAGS) -o bin/ServerConnection.o src/ServerConnection.cpp

bin/Game.o: src/Game.cpp
	g++ $(CFLAGS) -o bin/Game.o src/Game.cpp

bin/StompProtocol.o: src/StompProtocol.cpp
	g++ $(CFLAGS) -o bin/StompProtocol.o src/StompProtocol.cpp


.PHONY: clean
clean:
	rm -f bin/*
	