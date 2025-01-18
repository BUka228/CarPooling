package data.dao.postgres;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.UUID;


/**
 * Абстрактный класс для работы с базой данных PostgreSQL.
 */
public abstract class AbstractPostgresDao {
    protected final Connection connection;

    public AbstractPostgresDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Генерация нового UUID.
     *
     * @return уникальный идентификатор в виде строки.
     */
    protected String generateId() {
        return uuidToString(UUID.randomUUID());
    }

    /**
     * Преобразование строки в UUID.
     * @param id строка, представляющая UUID.
     * @return объект UUID.
     */
    protected UUID stringToUUID(String id)  {
        return UUID.fromString(id);
    }

    /**
     * Преобразование UUID в строку.
     */
    protected String uuidToString(@NotNull UUID id) {
        return id.toString();
    }
}

