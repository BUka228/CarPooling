package com.carpooling.exceptions.service;

/**
 * Исключение, если поездка не найдена (когда она ожидается).
 */
public class TripNotFoundException extends ServiceException {
    public TripNotFoundException(String message) {
        super(message);
    }
    public TripNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}