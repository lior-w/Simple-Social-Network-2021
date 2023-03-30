package bgu.spl.net.impl.BGS;

import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.api.Connections;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.Process;
import bgu.spl.net.impl.BGS.Messages.*;
import bgu.spl.net.impl.BGS.Messages.Error;

import javax.security.auth.callback.Callback;
import java.util.*;
import java.util.function.Function;

public class MessagingProtocolImpl implements BidiMessagingProtocol<Message> {

    private boolean shouldTerminate;
    private DataBase dataBase;
    private int connID;
    private Connections connections;
    private HashMap<Class<? extends Message>, Process> messageCallbacksMap;

    public MessagingProtocolImpl() {
        shouldTerminate = false;
        dataBase = DataBase.getInstance();
        connections = ConnectionsImpl.getInstance();
        messageCallbacksMap = new HashMap<>();
        messageCallbacksMap.put(Register.class, msg -> processRegister((Register) msg));
        messageCallbacksMap.put(Login.class, msg -> processLogin((Login) msg));
        messageCallbacksMap.put(Logout.class, msg -> processLogout((Logout) msg));
        messageCallbacksMap.put(Follow.class, msg -> processFollow((Follow) msg));
        messageCallbacksMap.put(Post.class, msg -> processPost((Post) msg));
        messageCallbacksMap.put(PM.class, msg -> processPM((PM) msg));
        messageCallbacksMap.put(Logstat.class, msg -> processLogstat((Logstat) msg));
        messageCallbacksMap.put(Stat.class, msg -> processStat((Stat) msg));
        messageCallbacksMap.put(Block.class, msg -> processBlock((Block) msg));
    }

    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public void start(int connectionId, Connections connections) {
        connID = connectionId;
        this.connections = connections;
    }

    public void process(Message msg) {
        messageCallbacksMap.get(msg.getClass()).process(msg);
    }

    private void processRegister(Register msg) {
        System.out.println("process register");
        Message m = null;
        try {
            dataBase.register(msg.getUserName(), msg.getPassword(), msg.getBirthday());

            m = new Ack(msg.getOpcode());
        }
        catch(Exception e){
            m = new Error(msg.getOpcode());
        }
        connections.send(connID, m);
//        connections.send(1, new Error((short)1));
    }

    private void processLogin(Login msg) {
        System.out.println("protocol LOGIN");
        Message m = null;
        try {
            dataBase.login(msg.getUserName(), msg.getPassword(), msg.getCaptcha(), connID);
            m = new Ack(msg.getOpcode());
        }
        catch(Exception e){
            m = new Error(msg.getOpcode());
        }
        connections.send(connID, m);
    }

    private void processLogout(Logout msg) {
        Message m = null;
        try {
            dataBase.logout(connID);
            m = new Ack(msg.getOpcode());
	    connections.send(connID, m);
	    connections.disconnect(connID);
	    shouldTerminate = true;
        } catch (Exception e) {
            m = new Error(msg.getOpcode());
	    connections.send(connID, m);
        }
        
    }

    private void processFollow(Follow msg) {
        Message m = null;
        try {
            dataBase.followUnfollow(msg.getFollow(), msg.getUserName(), connID);
            m = new Ack(msg.getOpcode(), msg.getUserName());
        } catch (Exception e) {
            m = new Error(msg.getOpcode());
            System.out.println(e.getMessage());
        }
        connections.send(connID, m);
    }

    private void processPost(Post msg) {
        Message m = null;
        Notification notif = null;
        List<String> destStr = msg.getDest();
        User myUser = dataBase.getUser(connID);
        if ( myUser == null || !dataBase.isLogged(connID)){
            m = new Error(msg.getOpcode());
            connections.send(connID, m);
            return;
        }
        List<User> folowers = myUser.getFollowers();
        List<User> dest = new LinkedList<>();
        for(String s : destStr)
            if (dataBase.isRegistered(s))
                dest.add(dataBase.getUser(s));
        for (User u : folowers)
            if(!dest.contains(u))
                dest.add(u);
        notif = new Notification(true, myUser.getUserName(), msg.getContent());
        dataBase.addPostsNpm(msg, myUser);
        for(User destUser : dest){
            System.out.println("sent post to : " + destUser.getUserName());
            if (!destUser.isUserBlocked(myUser)){
                if(!destUser.isLoggedIn()){
                    System.out.println(destUser.getUserName() + " isn't logged in");
                    dataBase.updateUserWhileLogOut(destUser, notif);
                }
                else {
                    int destConnId = dataBase.getConId(destUser);
                    connections.send(destConnId, notif);
                }
            }
        }
        m = new Ack(msg.getOpcode());
        connections.send(connID, m);
    }

    private void processPM(PM msg) {
        Message m = null;
        Notification notif = null;
        String destStr = msg.getUser();
        User destUser = dataBase.getUser(destStr);
        User myUser = dataBase.getUser(connID);
        if (destUser == null || myUser == null || destUser.isUserBlocked(myUser) || !dataBase.isLogged(connID)
                || !dataBase.isRegistered(destStr) || !myUser.isFollowing(destUser)){
            System.out.println("didnt send message cuz BLOCKED " + destUser.isUserBlocked(myUser));
            m = new Error(msg.getOpcode());
        }
        else{
            notif = new Notification(false, myUser.getUserName(), msg.getContent() + " " + msg.getDate());
            dataBase.addPostsNpm(msg, myUser);
            if(!destUser.isLoggedIn()){
                System.out.println(destUser.getUserName() + " isn't logged in");
                dataBase.updateUserWhileLogOut(destUser, notif);
            }
            else {
                int destConnId = dataBase.getConId(destUser);
                connections.send(destConnId, notif);
            }
            m = new Ack(msg.getOpcode());
        }
        connections.send(connID, m);
    }

    private void processLogstat(Logstat msg) {
        Message m = null;
        try {
            User myUser = dataBase.getUser(connID);
            if (myUser == null || !dataBase.isLogged(connID)) {
                m = new Error(msg.getOpcode());
                connections.send(connID, m);
                return;
            }

            Set<Integer> loggedUsers = dataBase.getLoggedUsersConnIdSet();
            for (int userId : loggedUsers) {
                System.out.println("logstat");
                short[] stat = dataBase.logstat(userId);
                m = new Ack(msg.getOpcode(), stat);
                connections.send(connID, m);
            }
        } catch (Exception e) {
            m = new Error(msg.getOpcode());
        }
    }

    private void processStat(Stat msg) {
        ArrayList<Ack> acks = new ArrayList<>();
        try {
            short[][] stats = dataBase.stat(msg.getListOfUsernames(), connID);
            for (short[] stat: stats) {
                acks.add(new Ack(msg.getOpcode(), stat));
            }
        } catch (Exception e) {
            connections.send(connID, new Error(msg.getOpcode()));
        }
        for (Ack ack: acks) {
            connections.send(connID, ack);
        }
    }

    public void processBlock(Block msg) {
        Message m = null;
        try {
            dataBase.blockUser(msg.getUserName(), connID);
            m = new Ack(msg.getOpcode());
        } catch (Exception e) {
            m = new Error(msg.getOpcode());
        }
        connections.send(connID, m);
    }

}
