package com.carpooling.exceptions.service;

public class HistoryContentServiceException extends Exception {
    public HistoryContentServiceException(String message) {
        super(message);
    }

    public HistoryContentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}