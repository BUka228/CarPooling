package com.carpooling.services.base;

import com.carpooling.entities.history.HistoryContent;
import com.carpooling.entities.enums.Status;
import com.carpooling.exceptions.service.HistoryContentServiceException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HistoryContentService {

    /**
     * Создание новой записи истории.
     *
     * @param historyContent Запись истории для создания.
     * @return ID созданной записи.
     * @throws HistoryContentServiceException Если произошла ошибка при создании.
     */
    String createHistoryContent(HistoryContent historyContent) throws HistoryContentServiceException;

    /**
     * Получение записи истории по ID.
     *
     * @param id ID записи истории.
     * @return Запись истории, если найдена.
     * @throws HistoryContentServiceException Если запись не найдена или произошла ошибка.
     */
    Optional<HistoryContent> getHistoryContentById(String id) throws HistoryContentServiceException;

    /**
     * Получение всех записей истории.
     *
     * @return Список всех записей истории.
     * @throws HistoryContentServiceException Если произошла ошибка при получении.
     */
    List<HistoryContent> getAllHistoryContents() throws HistoryContentServiceException;

    /**
     * Обновление записи истории.
     *
     * @param historyContent Запись истории с обновленными данными.
     * @throws HistoryContentServiceException Если произошла ошибка при обновлении.
     */
    void updateHistoryContent(HistoryContent historyContent) throws HistoryContentServiceException;

    /**
     * Удаление записи истории по ID.
     *
     * @param id ID записи истории.
     * @throws HistoryContentServiceException Если произошла ошибка при удалении.
     */
    void deleteHistoryContent(String id) throws HistoryContentServiceException;

    /**
     * Получение записей истории по имени класса.
     *
     * @param className Имя класса.
     * @return Список записей истории для указанного класса.
     * @throws HistoryContentServiceException Если произошла ошибка при получении.
     */
    List<HistoryContent> getHistoryContentsByClassName(String className) throws HistoryContentServiceException;

    /**
     * Получение записей истории по имени актора.
     *
     * @param actor Имя актора.
     * @return Список записей истории для указанного актора.
     * @throws HistoryContentServiceException Если произошла ошибка при получении.
     */
    List<HistoryContent> getHistoryContentsByActor(String actor) throws HistoryContentServiceException;

    /**
     * Получение записей истории по статусу.
     *
     * @param status Статус записи.
     * @return Список записей истории с указанным статусом.
     * @throws HistoryContentServiceException Если произошла ошибка при получении.
     */
    List<HistoryContent> getHistoryContentsByStatus(Status status) throws HistoryContentServiceException;

    /**
     * Получение записей истории по дате создания.
     *
     * @param createdDate Дата создания.
     * @return Список записей истории, созданных в указанную дату.
     * @throws HistoryContentServiceException Если произошла ошибка при получении.
     */
    List<HistoryContent> getHistoryContentsByCreatedDate(LocalDateTime createdDate) throws HistoryContentServiceException;
}