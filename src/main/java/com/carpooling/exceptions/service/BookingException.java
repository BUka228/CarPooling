package com.carpooling.exceptions.service;

/**
 * Исключение, возникающее при ошибках создания или управления бронированием
 * (например, нет мест, поездка не найдена).
 */
public class BookingException extends ServiceException {
    public BookingException(String message) {
        super(message);
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
}