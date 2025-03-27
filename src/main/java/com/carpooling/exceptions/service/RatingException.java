package com.carpooling.exceptions.service;

/**
 * Исключение, возникающее при ошибках создания или управления оценкой.
 */
public class RatingException extends ServiceException {
    public RatingException(String message) {
        super(message);
    }

    public RatingException(String message, Throwable cause) {
        super(message, cause);
    }
}