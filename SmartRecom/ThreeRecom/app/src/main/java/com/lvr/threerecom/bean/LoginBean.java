package com.lvr.threerecom.bean;

/**
 * Created by lvr on 2017/5/21.
 */

public class LoginBean {
    /**
     * userpasswd : 1
     * movie_preference : romance horror
     * music_preference : unknown
     * state : true
     * user_photo_url : null
     * nickname : a
     * userid : 1
     * sex : M
     * age : 1
     * career : technician
     * email : null
     * register_time : 2017-04-24
     * username : 1
     */

    private String userpasswd;
    private String movie_preference;
    private String music_preference;
    private boolean state;
    private String user_photo_url;
    private String nickname;
    private String userid;
    private String sex;
    private int age;
    private String career;
    private String email;
    private String register_time;
    private String username;

    public String getUserpasswd() {
        return userpasswd;
    }

    public void setUserpasswd(String userpasswd) {
        this.userpasswd = userpasswd;
    }

    public String getMovie_preference() {
        return movie_preference;
    }

    public void setMovie_preference(String movie_preference) {
        this.movie_preference = movie_preference;
    }

    public String getMusic_preference() {
        return music_preference;
    }

    public void setMusic_preference(String music_preference) {
        this.music_preference = music_preference;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getUser_photo_url() {
        return user_photo_url;
    }

    public void setUser_photo_url(String user_photo_url) {
        this.user_photo_url = user_photo_url;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegister_time() {
        return register_time;
    }

    public void setRegister_time(String register_time) {
        this.register_time = register_time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
