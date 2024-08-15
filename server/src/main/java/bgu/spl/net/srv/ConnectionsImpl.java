package bgu.spl.net.srv;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.io.IOException;
import java.util.ArrayList;
import bgu.spl.net.impl.stomp.Pair;

public class ConnectionsImpl<T> implements Connections<T> {

    //program should have 1 instance of this class
    
    //connectionId = clientId
    private AtomicInteger connectionIdCnt;
    private AtomicInteger messageIdCnt;
    private ReentrantReadWriteLock lock;
    private ReadLock readLock;
    private WriteLock writeLock;
    private HashMap<Integer, ConnectionHandler<T>> connectionId_ch; //must cause of send
    private HashMap<Pair, String> subId_topic; //must cause of unsubscribe
    private HashMap<Integer, ArrayList<Integer>> connectionId_subsId;
    private HashMap<String, ArrayList<Integer>> topic_connectionsId;
    private HashMap<Integer, ArrayList<String>> connectionId_topics;
    private HashMap<String, Integer> username_connectionId;
    private HashMap<String, String> username_passcode;
    private HashMap<Integer, String> connectionId_username;
    private HashMap<String, Boolean> username_isConnected;

    


    public ConnectionsImpl(){
        connectionIdCnt = new AtomicInteger();
        messageIdCnt = new AtomicInteger();
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
        connectionId_ch = new HashMap<>();
        username_connectionId = new HashMap<>();
        username_passcode = new HashMap<>();
        connectionId_username = new HashMap<>();
        subId_topic = new HashMap<>();
        username_isConnected = new HashMap<>();
        topic_connectionsId = new HashMap<>();
        connectionId_topics = new HashMap<>();
        connectionId_subsId = new HashMap<>();
    }

    @Override
    public boolean send(int connectionId, T msg) { //why boolean?
        // TODO Auto-generated method stub
        readLock.lock();
        try{
            if(connectionId_ch.get(connectionId) != null)
                connectionId_ch.get(connectionId).send(msg);
            
        } finally { readLock.unlock(); }
        return false;
    }

    @Override
    public void send(String channel, T msg) {
        // TODO Auto-generated method stub
        readLock.lock();
        try{
            ArrayList<Integer> clients = topic_connectionsId.get(channel);
            for(Integer connectionId : clients)
                connectionId_ch.get(connectionId).send(msg);
        }finally{ readLock.unlock();}
    }

    @Override
    public void disconnect(int connectionId) {
        // TODO Auto-generated method stub
        writeLock.lock();
        try{
            try{
                if(connectionId_ch.get(connectionId) != null)
                    connectionId_ch.get(connectionId).close();
            }catch(IOException e){};
            connectionId_ch.remove(connectionId);
            String username = connectionId_username.remove(connectionId);
            username_isConnected.replace(username, true, false);
            if(connectionId_subsId.get(connectionId) != null && !connectionId_subsId.get(connectionId).isEmpty()){
            ArrayList<Integer> subsId = new ArrayList<Integer>(connectionId_subsId.get(connectionId));
                for(Integer subId : subsId){
                    unsubscribe(subId, connectionId);
                    Pair check = new Pair(connectionId, username);
                    for(Pair curr : subId_topic.keySet()){
                        if (curr.equals(check)){
                            check = curr;
                            break;
                        }
                    subId_topic.remove(check);
                 }
                }
            }
        } finally{ writeLock.unlock(); }
    }


    public void subscribe(String _subId, String topic, int connectionId){
        writeLock.lock();
        try{
            Integer subId = Integer.parseInt(_subId);
            String user = connectionId_username.get(connectionId);
            if(topic_connectionsId.get(topic) == null)
                topic_connectionsId.put(topic, new ArrayList<>());    
            topic_connectionsId.get(topic).add(connectionId);

            if(connectionId_topics.get(connectionId) == null)
                connectionId_topics.put(connectionId, new ArrayList<>());
            connectionId_topics.get(connectionId).add(topic);

            if(connectionId_subsId.get(connectionId) == null)
                connectionId_subsId.put(connectionId, new ArrayList<>());
            connectionId_subsId.get(connectionId).add(subId);

            subId_topic.put(new Pair(subId,user), topic);
        } finally{ writeLock.unlock(); }
    }
    public boolean unsubscribe(int subId, int connectionId){
        boolean found;
        writeLock.lock();
        try{
            String user = connectionId_username.get(connectionId);
            Pair check = new Pair(subId, user);
            for(Pair curr : subId_topic.keySet()){
                if (curr.equals(check)){
                    check = curr;
                    break;
                }
            }
            if(subId_topic.get(check) == null)
                found = false;
            else{
                found = true;
                String topic = subId_topic.remove(check);
                topic_connectionsId.get(topic).remove(topic_connectionsId.get(topic).indexOf(connectionId));
                connectionId_topics.get(connectionId).remove(connectionId_topics.get(connectionId).indexOf(topic));
                connectionId_subsId.get(connectionId).remove( connectionId_subsId.get(connectionId).indexOf(subId));
            }
        }finally{ writeLock.unlock(); }
        return found;
    }

    public int connect(String passcode, String username, int connectionId){
        writeLock.lock();
        try{
            if(username_passcode.get(username) == null){ //new username
                username_passcode.put(username, passcode);
                username_connectionId.put(username, connectionId);
                connectionId_username.put(connectionId, username);
                username_isConnected.put(username, true);
                return 0;
            }
            else if(!(username_isConnected.get(username)) && (username_passcode.get(username).equals(passcode))){
                username_isConnected.replace(username, false, true);
                username_connectionId.put(username, connectionId);
                connectionId_username.put(connectionId, username);
                return 0;               
            }
        }finally { writeLock.unlock(); }
        readLock.lock();
            try{
                if(!(username_passcode.get(username).equals(passcode))){ //wrong passcode
                    return 1;
            }
        } finally{ readLock.unlock(); }
         //user is already connected
        return 2;
    }

    public int getConnectionsId(){
        return connectionIdCnt.getAndIncrement();
     }

    public ArrayList<Integer> getClientsByTopic(String topic){
        readLock.lock();
        try{
            return topic_connectionsId.get(topic);
        } finally { readLock.unlock(); }
    }

    public boolean isSubscribed(int connectionId, String topic){
        readLock.lock();
        try{
            return connectionId_topics.get(connectionId) != null && connectionId_topics.get(connectionId).contains(topic);
        } finally { readLock.unlock(); }
        
    }

    public boolean newSubId(String subId, int connectionId) {
        readLock.lock();
        try{
            return connectionId_subsId.get(connectionId) == null || !connectionId_subsId.get(connectionId).contains(Integer.parseInt(subId));
        } finally { readLock.unlock(); }
    }

    public int getMessageId(){
        return messageIdCnt.getAndIncrement();
    }

    public int getSubIdByTopic(String topic, String username){
        int conId = username_connectionId.get(username);
        int output = -1;
        ArrayList<Integer> subsId = connectionId_subsId.get(conId);
        for(Integer subId : subsId){
            Pair check = new Pair(subId, username);
            for(Pair curr : subId_topic.keySet()){
                if (curr.equals(check)){
                    check = curr;
                    break;
                }
            }
            if(subId_topic.get(check).equals(topic))
                return subId;
        }
        return output;
    }
    public void addConnectionHnadlerToConnectionId(int id, ConnectionHandler<T> ch){
        connectionId_ch.put(id, ch);
    }   
    
}