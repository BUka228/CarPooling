package com.carpooling.exceptions.service;

/**
 * Исключение, возникающее при ошибках создания или управления поездкой.
 */
public class TripException extends ServiceException {
    public TripException(String message) {
        super(message);
    }

    public TripException(String message, Throwable cause) {
        super(message, cause);
    }
}