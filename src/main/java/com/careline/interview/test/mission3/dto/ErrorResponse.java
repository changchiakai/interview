package com.careline.interview.test.mission3.dto;

public class ErrorResponse {
    private String msg;

    public ErrorResponse(String msg) {
        this.msg = msg;
    }

    // getter
    public String getMsg() {
        return msg;
    }
}
