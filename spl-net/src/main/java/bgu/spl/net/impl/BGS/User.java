package bgu.spl.net.impl.BGS;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {

    private String userName;
    private String password;
    private LocalDate birthday;
    private boolean loggedIn;
    private int posts;
    private List<User> followers;
    private List<User> following;
    private List<User> blockedUsers;

    public User(String userName, String password, String birthday) {
        this.userName = userName;
        this.password = password;
        String[] bday = birthday.split("-");
        if (bday.length == 3) {
            this.birthday = LocalDate.of(Integer.parseInt(bday[2]), Integer.parseInt(bday[1]), Integer.parseInt(bday[0]));
        }
        this.loggedIn = false;
        this.posts = 0;
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.blockedUsers = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public int getPosts() {
        return posts;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public List<User> getFollowers(){
        return followers;
    }

    public List<User> getFollowing() {
        return following;
    }

    public boolean isUserBlocked(User user) {
        return blockedUsers.contains(user);
    }

    public void login(String pass) throws Exception {
        if (!loggedIn) {
            if (pass.equals(password)) {
                loggedIn = true;
            } else throw new Exception("password doesn't match");
        } else throw new Exception("user is already logged in");
    }

    public void logout() throws Exception {
        if (loggedIn) {
            loggedIn = false;
        } else throw new Exception("user isn't logged in");
    }

    public void follow(User user) throws Exception {
        if (loggedIn) {
            if (!following.contains(user)) {
                following.add(user);
                user.followMe(this);
                System.out.println(this.userName + " started following " + following.get(0).getUserName());
            } else throw new Exception("you already follow that user");
        } else throw new Exception("user isn't logged in");
    }

    public void followMe(User user) {
        followers.add(user);
        System.out.println(this.userName + " being followed by " + followers.get(0).userName);
    }

    public void unfollow(User user) throws Exception {
        if (loggedIn) {
            if (following.contains(user)) {
                following.remove(user);
                user.unFollowMe(this);
            } else throw new Exception("you haven't follow that user");
        } else throw new Exception("user isn't logged in");
    }

    public void unFollowMe(User user) {
        followers.remove(user);
    }

    public void block(User user) throws Exception {
        if (loggedIn) {
            if (!blockedUsers.contains(user)) {
                blockedUsers.add(user);
            } else throw new Exception("you already blocked that user");
        } else throw new Exception("user isn't logged in");
    }
    public boolean isFollowing(User u){
        return following.contains(u);
    }
    public void post(){posts++;}
}
