package com.carpooling.exceptions.service;

public class BookingServiceException extends Exception {
    public BookingServiceException(String message) {
        super(message);
    }

    public BookingServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}