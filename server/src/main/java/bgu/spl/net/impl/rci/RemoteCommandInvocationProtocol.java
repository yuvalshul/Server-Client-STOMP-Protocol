package bgu.spl.net.impl.rci;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Connections;

import java.io.Serializable;

public class RemoteCommandInvocationProtocol<T> implements MessagingProtocol<Serializable> {

    private T arg;

    public RemoteCommandInvocationProtocol(T arg) {
        this.arg = arg;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Serializable process(Serializable msg) {
        return ((Command) msg).execute(arg);
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }

    @Override
    public void start(int connectionsId, Connections<Serializable> connections) {}

}
