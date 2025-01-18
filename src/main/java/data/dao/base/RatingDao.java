package data.dao.base;

import data.model.record.RatingRecord;
import exceptions.dao.DataAccessException;

import java.util.Optional;

/**
 * Интерфейс для работы с рейтингами.
 */
public interface RatingDao {
    /**
     * Создает новый рейтинг.
     * @param ratingRecord Объект рейтинга, содержащий информацию о рейтинге.
     * @return ID созданного рейтинга.
     * @throws DataAccessException Если произошла ошибка при создании рейтинга.
     */
    String createRating(RatingRecord ratingRecord) throws DataAccessException;
    /**
     * Возвращает рейтинг по его ID.
     * @param id ID рейтинга.
     * @return Объект рейтинга.
     * @throws DataAccessException Если рейтинг не найден.
     */
    Optional<RatingRecord> getRatingById(String id) throws DataAccessException;
    /**
     * Обновляет существующий рейтинг.
     * @param ratingRecord Объект рейтинга с обновленными данными.
     * @throws DataAccessException Если произошла ошибка при обновлении рейтинга.
     */
    void updateRating(RatingRecord ratingRecord) throws DataAccessException;
    /**
     * Удаляет рейтинг по его ID.
     * @param id ID рейтинга.
     * @throws DataAccessException Если произошла ошибка при удалении рейтинга.
     */
    void deleteRating(String id) throws DataAccessException;
}
