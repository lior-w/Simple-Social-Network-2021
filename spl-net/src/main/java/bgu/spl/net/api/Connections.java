package bgu.spl.net.api;

public interface Connections<T> {

    boolean send(int connId, T msg);

    void broadcast(T msg);

    void disconnect(int connId);
}
