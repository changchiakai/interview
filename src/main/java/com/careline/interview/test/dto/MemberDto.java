package com.careline.interview.test.dto;

public class MemberDto {
    private int memberId;
    private String email;
    private String name;

    // Constructor
    public MemberDto(int memberId, String email, String name) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
    }

    // Getter 和 Setter
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
