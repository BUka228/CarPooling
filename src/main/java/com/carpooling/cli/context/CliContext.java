package com.carpooling.cli.context;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static com.carpooling.constants.Constants.*;

@Slf4j
public class CliContext {

    public enum StorageType {
        XML, CSV, MONGO, POSTGRES
    }

    @Getter
    private static String currentUserId;

    private static StorageType currentStorageType;

    // Статический блок для загрузки настроек при старте
    static {
        loadPreferences();
    }

    private static void loadPreferences() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE_NAME);
        // Загружаем тип хранилища, по умолчанию POSTGRES
        String storedType = prefs.get(STORAGE_TYPE_KEY, StorageType.POSTGRES.name());
        try {
            currentStorageType = StorageType.valueOf(storedType);
            log.info("Loaded storage type preference: {}", currentStorageType);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid stored storage type '{}'. Using default: {}", storedType, StorageType.POSTGRES);
            currentStorageType = StorageType.POSTGRES; // Возврат к дефолту при ошибке
            // Можно удалить некорректную настройку
            prefs.remove(STORAGE_TYPE_KEY);
            try {
                prefs.flush();
            } catch (BackingStoreException bse) {
                log.error("Failed to flush preferences after removing invalid key", bse);
            }
        }

        // Загружаем ID пользователя (если он был сохранен)
        currentUserId = prefs.get(USER_ID_KEY, null);
        if (currentUserId != null) {
            log.info("Loaded current user ID: {}", currentUserId);
        }
    }

    // --- Метод для сохранения настроек ---
    private static void savePreferences() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE_NAME);
        if (currentStorageType != null) {
            prefs.put(STORAGE_TYPE_KEY, currentStorageType.name());
        } else {
            prefs.remove(STORAGE_TYPE_KEY); // Удаляем, если null (маловероятно)
        }

        if (currentUserId != null) {
            prefs.put(USER_ID_KEY, currentUserId);
        } else {
            prefs.remove(USER_ID_KEY); // Удаляем, если пользователь вышел
        }

        try {
            prefs.flush(); // Сохраняем изменения
            log.debug("Preferences saved.");
        } catch (BackingStoreException e) {
            log.error("Failed to save preferences", e);
            // Не критично для работы, но нужно залогировать
        }
    }

    // --- Геттеры и Сеттеры (с сохранением) ---

    public static void setCurrentUserId(String userId) {
        if (userId == null && currentUserId == null) return; // Нечего менять
        if (userId != null && userId.equals(currentUserId)) return; // Нечего менять

        log.debug("Setting current user ID to: {}", userId == null ? "null" : userId);
        currentUserId = userId;
        savePreferences(); // Сохраняем при изменении
    }

    public static StorageType getCurrentStorageType() {
        // Если вдруг не загрузился в static блоке
        if (currentStorageType == null) {
            loadPreferences(); // Повторная попытка загрузки
            if (currentStorageType == null) { // Если все равно null
                log.error("Storage type is still null after reload! Falling back to default.");
                currentStorageType = StorageType.POSTGRES; // Безопасный дефолт
            }
        }
        return currentStorageType;
    }

    public static void setCurrentStorageType(StorageType storageType) {
        if (storageType == null) {
            log.warn("Attempted to set null storage type. Ignoring.");
            return;
        }
        if (storageType == currentStorageType) return; // Нечего менять

        log.info("Setting current storage type to: {}", storageType);
        currentStorageType = storageType;
        savePreferences(); // Сохраняем при изменении
    }

}