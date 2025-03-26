package com.carpooling.entities.enums;


public enum BookingStatus {
    PENDING,    // В ожидании подтверждения
    CONFIRMED,  // Подтверждено
    CANCELLED,  // Отменено
    COMPLETED   // Завершено (например, после поездки)
}