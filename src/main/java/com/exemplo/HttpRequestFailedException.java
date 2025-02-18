package com.exemplo;

public class HttpRequestFailedException extends RuntimeException {
    private final int statusCode;

    public HttpRequestFailedException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}