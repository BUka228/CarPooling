package com.carpooling.exceptions.service;

/**
 * Исключение, если пользователь не найден (когда он ожидается).
 */
public class UserNotFoundException extends ServiceException {
    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}