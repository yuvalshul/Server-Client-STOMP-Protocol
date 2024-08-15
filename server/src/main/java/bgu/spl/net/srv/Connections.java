package bgu.spl.net.srv;

import java.util.ArrayList;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void send(String channel, T msg);

    void disconnect(int connectionId);

    void subscribe(String _subId, String topic, int connectionId); //our func

    boolean unsubscribe(int subId, int connectionId); //our func

    int connect(String passcode, String username, int connectionId); //our func

    int getSubIdByTopic(String topic, String username); //our func

    int getConnectionsId();

    boolean isSubscribed(int connectionId, String topic);

    int getMessageId();

    ArrayList<Integer> getClientsByTopic(String topic);

    boolean newSubId(String id, int connectionId);
    
    void addConnectionHnadlerToConnectionId(int id, ConnectionHandler<T> ch);


}

