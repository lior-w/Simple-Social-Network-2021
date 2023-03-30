package bgu.spl.net.api;

public interface BidiMessagingProtocol<T> {

    void start(int connectionId, Connections connections);

    void process(T msg);

    boolean shouldTerminate();
}
