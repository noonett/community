package com.nowcoder.community.exception;

public class RatelimiterException extends Exception {
    private String message;

    public RatelimiterException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
