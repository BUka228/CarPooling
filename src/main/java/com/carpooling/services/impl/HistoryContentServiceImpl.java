package com.carpooling.services.impl;

import com.carpooling.dao.base.HistoryContentDao;
import com.carpooling.entities.history.HistoryContent;
import com.carpooling.entities.history.Status;
import com.carpooling.exceptions.service.HistoryContentServiceException;
import com.carpooling.services.base.HistoryContentService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class HistoryContentServiceImpl implements HistoryContentService {

    private final HistoryContentDao historyContentDao;

    public HistoryContentServiceImpl(HistoryContentDao historyContentDao) {
        this.historyContentDao = historyContentDao;
    }

    @Override
    public String createHistoryContent(HistoryContent historyContent) throws HistoryContentServiceException {
        log.info("Создание записи истории: {}", historyContent);
        try {
            String id = historyContentDao.createHistory(historyContent);
            log.info("Запись истории успешно создана: {}", id);
            return id;
        } catch (Exception e) {
            log.error("Ошибка при создании записи истории: {}", e.getMessage());
            throw new HistoryContentServiceException("Ошибка при создании записи истории", e);
        }
    }

    @Override
    public Optional<HistoryContent> getHistoryContentById(String id) throws HistoryContentServiceException {
        log.info("Поиск записи истории по ID: {}", id);
        try {
            Optional<HistoryContent> historyContent = historyContentDao.getHistoryById(id);
            if (historyContent.isEmpty()) {
                log.warn("Запись истории не найдена: {}", id);
            }
            log.info("Запись истории найдена: {}", id);
            return historyContent;
        } catch (Exception e) {
            log.error("Ошибка при поиске записи истории: {}", e.getMessage());
            throw new HistoryContentServiceException("Ошибка при поиске записи истории", e);
        }
    }

    @Override
    public List<HistoryContent> getAllHistoryContents() throws HistoryContentServiceException {
        log.info("Получение всех записей истории");
        try {
            List<HistoryContent> historyContents = Collections.emptyList();
            log.info("Все записи истории успешно получены");
            return historyContents;
        } catch (Exception e) {
            log.error("Ошибка при получении всех записей истории: {}", e.getMessage());
            throw new HistoryContentServiceException("Ошибка при получении всех записей истории", e);
        }
    }

    @Override
    public void updateHistoryContent(@NotNull HistoryContent historyContent) throws HistoryContentServiceException {
        try {
            log.info("Обновление записи истории: {}", historyContent.getId());
            historyContentDao.updateHistory(historyContent);
            log.info("Запись истории успешно обновлена: {}", historyContent.getId());
        } catch (Exception e) {
            log.error("Ошибка при обновлении записи истории: {}", e.getMessage());
            throw new HistoryContentServiceException("Ошибка при обновлении записи истории", e);
        }
    }

    @Override
    public void deleteHistoryContent(String id) throws HistoryContentServiceException {
        log.info("Удаление записи истории: {}", id);
        try {
            historyContentDao.deleteHistory(id);
            log.info("Запись истории успешно удалена: {}", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении записи истории: {}", e.getMessage());
            throw new HistoryContentServiceException("Ошибка при удалении записи истории", e);
        }
    }

    @Override
    public List<HistoryContent> getHistoryContentsByClassName(String className) throws HistoryContentServiceException {
        log.info("Поиск записей истории по имени класса: {}", className);
        try {
            List<HistoryContent> historyContents = Collections.emptyList();
            log.info("Записи истории для класса {} успешно получены", className);
            return historyContents;
        } catch (Exception e) {
            log.error("Ошибка при поиске записей истории по имени класса: {}", e.getMessage());
            throw new HistoryContentServiceException("Ошибка при поиске записей истории по имени класса", e);
        }
    }

    @Override
    public List<HistoryContent> getHistoryContentsByActor(String actor) throws HistoryContentServiceException {
        log.info("Поиск записей истории по имени актора: {}", actor);
        try {
            List<HistoryContent> historyContents = Collections.emptyList();
            log.info("Записи истории для актора {} успешно получены", actor);
            return historyContents;
        } catch (Exception e) {
            log.error("Ошибка при поиске записей истории по имени актора: {}", e.getMessage());
            throw new HistoryContentServiceException("Ошибка при поиске записей истории по имени актора", e);
        }
    }

    @Override
    public List<HistoryContent> getHistoryContentsByStatus(Status status) throws HistoryContentServiceException {
        log.info("Поиск записей истории по статусу: {}", status);
        try {
            List<HistoryContent> historyContents = Collections.emptyList();
            log.info("Записи истории со статусом {} успешно получены", status);
            return historyContents;
        } catch (Exception e) {
            log.error("Ошибка при поиске записей истории по статусу: {}", e.getMessage());
            throw new HistoryContentServiceException("Ошибка при поиске записей истории по статусу", e);
        }
    }

    @Override
    public List<HistoryContent> getHistoryContentsByCreatedDate(LocalDateTime createdDate) throws HistoryContentServiceException {
        log.info("Поиск записей истории по дате создания: {}", createdDate);
        try {
            List<HistoryContent> historyContents = Collections.emptyList();
            log.info("Записи истории за дату {} успешно получены", createdDate);
            return historyContents;
        } catch (Exception e) {
            log.error("Ошибка при поиске записей истории по дате создания: {}", e.getMessage());
            throw new HistoryContentServiceException("Ошибка при поиске записей истории по дате создания", e);
        }
    }
}
