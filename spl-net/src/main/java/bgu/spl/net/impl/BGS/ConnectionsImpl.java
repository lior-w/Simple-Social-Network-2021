package bgu.spl.net.impl.BGS;

import bgu.spl.net.api.Connections;
import bgu.spl.net.srv.ConnectionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> clients;
    private int conId;

    public static ConnectionsImpl getInstance() {
        return ConnectionsImplHolder.instance;
    }

    private static class ConnectionsImplHolder {
        private static ConnectionsImpl instance = new ConnectionsImpl();
    }

    private ConnectionsImpl() {
        clients = new ConcurrentHashMap<>();
        conId = 0;
    }

    public synchronized boolean send(int connId, T msg){
        if (!clients.containsKey(connId))
            return false;
        ConnectionHandler<T> ch = clients.get(connId);
        System.out.println(ch.id() + " send");
        ch.send(msg);

        return true;
    }

    public synchronized void broadcast(T msg){
        for(ConnectionHandler c : clients.values())
            c.send(msg);
    }

    public synchronized void disconnect(int connId){
	//try {
        	clients.remove(connId);//.close();
	//} catch (IOException ignore) {}
    }	

    public synchronized int getID() {
        int ID = conId;
        conId++;
        return ID;
    }

    public synchronized void addConnectionHandler(ConnectionHandler ch, int conId){
        clients.put(conId,ch);
    }

}
