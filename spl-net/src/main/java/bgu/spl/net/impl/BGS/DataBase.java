package bgu.spl.net.impl.BGS;

import bgu.spl.net.impl.BGS.Messages.Message;
import bgu.spl.net.impl.BGS.Messages.Notification;
import bgu.spl.net.impl.BGS.Messages.Post;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataBase {

    private ConcurrentHashMap<String, User> nameUserMap;
    private ConcurrentHashMap<Integer, User> conIdUserMap;
    private ConcurrentHashMap<User, Integer> userConIdMap;
    private List<String> filteredWords;
    private List<Message> postsNpm;
    private ConcurrentHashMap<User, Queue<Notification>> updateWhileLogOut;

    private static class DataBaseHolder {
        private static DataBase instance = new DataBase();
    }

    private DataBase() {
        nameUserMap = new ConcurrentHashMap<>();
        conIdUserMap = new ConcurrentHashMap<>();
        userConIdMap = new ConcurrentHashMap<>();
        filteredWords = new LinkedList<>();
        filteredWords.add("war");
        filteredWords.add("Trump");
        filteredWords.add("duck");
        filteredWords.add("liorHaefes");
        postsNpm = new LinkedList<>();
        updateWhileLogOut = new ConcurrentHashMap<>();
    }
    public List<Message> getPostsNpm(){return postsNpm;}
    public void addPostsNpm(Message m, User u){
        postsNpm.add(m);
        if(m instanceof Post){
            u.post();
        }

    }
    public List<String> getFilteredWords(){return filteredWords;}
    public boolean isLogged(int connId){return conIdUserMap.containsKey(connId);}
    public boolean isRegistered(String userName){return nameUserMap.containsKey(userName);}
    public int getConId(User u){return userConIdMap.get(u);}
    public static DataBase getInstance() {
        return DataBaseHolder.instance;
    }

    public synchronized void register(String userName, String password, String birthday) throws Exception {
        System.out.println("data base register");
        if (nameUserMap.containsKey(userName))
            throw new Exception("user already exist");
        else {
            User user = new User(userName, password, birthday);
            nameUserMap.put(userName, user);
            System.out.println("nameUserMap added " + userName);
            updateWhileLogOut.put(user, new ConcurrentLinkedQueue<>());
        }
    }

    public synchronized void login(String userName, String password, Byte captcha, int conId) throws Exception {
        System.out.println("data base LOGIN");
        if (nameUserMap.containsKey(userName)) {
            if (!conIdUserMap.containsKey(conId)) {
                if (captcha == '1') {
                    User user = nameUserMap.get(userName);
                    user.login(password);
                    conIdUserMap.put(conId, user);
                    userConIdMap.put(user, conId);
                    for(Notification notif : updateWhileLogOut.get(user)){
                        int destConnId = getConId(user);
                        ConnectionsImpl.getInstance().send(destConnId, notif);
                    }
                    updateWhileLogOut.remove(user);
                    System.out.println("userConIdMap added " + userName + " conId = " + conId);
                } else throw new Exception("captcah is zero");
            } else throw new Exception("you are already connected");
        } else throw new Exception("user doesn't exist");
    }

    public User getUser(int connId) {
        return conIdUserMap.get(connId);
    }

    public User getUser(String name) {
        return nameUserMap.get(name);
    }

    public synchronized void logout(int conId) throws Exception {
        if (conIdUserMap.containsKey(conId)) {
            User user = conIdUserMap.get(conId);
            updateWhileLogOut.put(user, new ConcurrentLinkedQueue<>());
            user.logout();
            conIdUserMap.remove(conId, user);
            userConIdMap.remove(user, conId);
        } else throw new Exception("no user is logged in");
    }

    public synchronized void followUnfollow(byte followUnfollow, String userName, int conId) throws Exception {
        if (conIdUserMap.containsKey(conId)) {
            User me = conIdUserMap.get(conId);
            if (nameUserMap.containsKey(userName)) {
                User other = nameUserMap.get(userName);
                if (followUnfollow != 49) {
                    if (!me.getFollowing().contains(other)) {
                        me.follow(other);
                        System.out.println(userName + "");
                    } else throw new Exception("you already follow that user");
                } else {
                    if (me.getFollowing().contains(other)) {
                        me.unfollow(other);
                    } else throw new Exception("you don't follow that user");
                }
            } else throw new Exception("the user you want to follow doesn't exist");
        } else throw new Exception("no user is logged in");
    }

    public synchronized void blockUser(String userName, int conId) throws Exception {
        if (conIdUserMap.containsKey(conId)) {
            if (nameUserMap.containsKey(userName)) {
                User user = nameUserMap.get(userName);
                User user1 = conIdUserMap.get(conId);
                if(user.isFollowing(user1))
                    user.unfollow(user1);
                if(user1.isFollowing(user))
                    user1.unfollow(user);
                conIdUserMap.get(conId).block(user);
            } else throw new Exception("user doesn't exist");
        } else throw new Exception("no user is logged in");
    }

    public synchronized short[] logstat(int conId) throws Exception {
        short[] args = new short[4];
        if (conIdUserMap.containsKey(conId)) {
            User user = conIdUserMap.get(conId);
            Period period = Period.between(user.getBirthday(), LocalDate.now());
            args[0] = (short) period.getYears();
            args[1] = (short) user.getPosts();
            args[2] = (short) user.getFollowers().size();
            args[3] = (short) user.getFollowing().size();
        } else throw new Exception("no user is logged in");
        return args;
    }

public synchronized short[][] stat(String[] userNames, int conId) throws Exception {
        if (conIdUserMap.containsKey(conId)) {
            short[][] stats = new short[userNames.length][4];
            for (int i = 0; i < userNames.length; i++) {
                if (nameUserMap.containsKey(userNames[i])) {
                    User user = nameUserMap.get(userNames[i]);
                    Period period = Period.between(user.getBirthday(), LocalDate.now());
                    stats[i][0] = (short) period.getYears();
                    stats[i][1] = (short) user.getPosts();
                    stats[i][2] = (short) user.getFollowers().size();
                    stats[i][3] = (short) user.getFollowing().size();
                } else throw new Exception("user doesn't exist");
            }
            return stats;
        } else throw new Exception("no user is logged in");
    }
    public Set<Integer> getLoggedUsersConnIdSet() {
        return conIdUserMap.keySet();
    }
    public boolean updateUserWhileLogOut(User u, Notification notif){
        try {
            updateWhileLogOut.get(u).add(notif);
            return true;
        }catch (Exception e){
            return false;
        }

    }
}
