package com.carpooling.exceptions.service;

/**
 * Исключение, возникающее при ошибках создания или управления маршрутом.
 */
public class RouteException extends ServiceException {
    public RouteException(String message) {
        super(message);
    }

    public RouteException(String message, Throwable cause) {
        super(message, cause);
    }
}