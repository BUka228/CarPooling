package com.carpooling.exceptions.service;

/**
 * Базовое исключение для ошибок сервисного слоя.
 * Можно использовать, если более специфичное исключение не подходит.
 */
public class ServiceException extends Exception {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}