package com.careline.interview.test.dto;

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
