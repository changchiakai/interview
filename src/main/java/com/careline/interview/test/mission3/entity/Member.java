package com.careline.interview.test.mission3.entity;
public class Member {
    private int id;
    private String email;
    private String password;
    private String nickname;

    // Constructors, Getters, and Setters
    public Member(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    // Constructors, Getters, and Setters
    public Member(int id , String email, String password, String nickname) {
        this.id =id ;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }


    public Member() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
