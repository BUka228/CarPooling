package com.carpooling.exceptions.service;

/**
 * Исключение, возникающее при ошибках регистрации пользователя
 * (например, email уже занят, невалидные данные).
 */
public class RegistrationException extends ServiceException {
    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}