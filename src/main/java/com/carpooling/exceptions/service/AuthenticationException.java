package com.carpooling.exceptions.service;

/**
 * Исключение, возникающее при ошибках аутентификации
 * (например, пользователь не найден, неверный пароль).
 */
public class AuthenticationException extends ServiceException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}