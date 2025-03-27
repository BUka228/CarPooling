package com.carpooling.exceptions.service;


/**
 * Исключение, указывающее, что запрошенная операция
 * не поддерживается текущей реализацией DAO или конфигурацией.
 */
public class OperationNotSupportedException extends ServiceException {
    public OperationNotSupportedException(String message) {
        super(message);
    }

    public OperationNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}