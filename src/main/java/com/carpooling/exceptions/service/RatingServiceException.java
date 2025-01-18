package com.carpooling.exceptions.service;

public class RatingServiceException extends Exception {
    public RatingServiceException(String message) {
        super(message);
    }

    public RatingServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}