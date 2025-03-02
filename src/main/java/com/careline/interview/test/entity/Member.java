package com.careline.interview.test.entity;

public class Member {
    private int member_id;
    private String email;
    private String password;
    private String name;
    public Member(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public Member(int member_id, String email, String password, String name) {
        this.member_id = member_id;
        this.email = email;
        this.password = password;
        this.name = name;
    }
    public Member(int member_id,String name) {
        this.member_id = member_id;
        this.name = name;
    }

    public Member() {
    }


    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
