package com.careline.interview.test.mission3.dto;

// 註冊成功回應資料
public class RegisterResponse {
    private int memberId;

    public RegisterResponse(int memberId) {
        this.memberId = memberId;
    }

    // getter
    public int getMemberId() {
        return memberId;
    }
}
