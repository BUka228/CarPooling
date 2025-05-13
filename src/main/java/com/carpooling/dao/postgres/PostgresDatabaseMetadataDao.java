package com.carpooling.dao.postgres;

import com.carpooling.dao.base.DatabaseMetadataDao;
import com.carpooling.exceptions.dao.DataAccessException;
// Импортируем класс Constants для доступа к константам SQL
import com.carpooling.constants.Constants;
import jakarta.persistence.PersistenceException;
import lombok.AllArgsConstructor; // Добавим, раз уж используется @AllArgsConstructor
import lombok.extern.slf4j.Slf4j; // Заменим org.slf4j.Logger и LoggerFactory
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class PostgresDatabaseMetadataDao implements DatabaseMetadataDao {

    private final SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public String getDatabaseProductNameAndVersion() throws DataAccessException {
        try {
            return getCurrentSession().doReturningWork(connection -> {
                DatabaseMetaData metaData = connection.getMetaData();
                return metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion();
            });
        } catch (PersistenceException e) {
            log.error("Error getting database product name and version", e);
            throw new DataAccessException("Error getting database product name and version", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getTableNames() throws DataAccessException {
        try {
            NativeQuery<String> query = getCurrentSession().createNativeQuery(Constants.GET_TABLE_NAMES_SQL, String.class);
            return query.list();
        } catch (PersistenceException e) {
            log.error("Error getting table names", e);
            throw new DataAccessException("Error getting table names", e);
        }
    }

    @Override
    public long getTableRowCount(String tableName) throws DataAccessException {
        if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
            log.warn("Invalid table name format received: {}", tableName);
            throw new DataAccessException("Invalid table name format: " + tableName);
        }
        String sql = String.format(Constants.GET_TABLE_ROW_COUNT_SQL_TEMPLATE, tableName);
        try {
            NativeQuery<Number> query = getCurrentSession().createNativeQuery(sql, Number.class);
            Number result = query.uniqueResult();
            return result != null ? result.longValue() : 0L;
        } catch (PersistenceException e) {
            log.error("Error getting row count for table {}", tableName, e);
            throw new DataAccessException("Error getting row count for table " + tableName, e);
        }
    }

    @Override
    public Map<String, String> getTableColumnInfo(String tableName) throws DataAccessException {
        if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
            log.warn("Invalid table name format received: {}", tableName);
            throw new DataAccessException("Invalid table name format: " + tableName);
        }
        Map<String, String> columnInfo = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            NativeQuery<Object[]> query = getCurrentSession().createNativeQuery(Constants.GET_TABLE_COLUMN_INFO_SQL, Object[].class);
            query.setParameter("tableName", tableName);
            List<Object[]> results = query.list();
            for (Object[] row : results) {
                columnInfo.put((String) row[0], (String) row[1]);
            }
            return columnInfo;
        } catch (PersistenceException e) {
            log.error("Error getting column info for table {}", tableName, e);
            throw new DataAccessException("Error getting column info for table " + tableName, e);
        }
    }

    @Override
    public String getDatabaseSize() throws DataAccessException {
        try {
            NativeQuery<String> query = getCurrentSession().createNativeQuery(Constants.GET_DATABASE_SIZE_SQL, String.class);
            return query.uniqueResult();
        } catch (PersistenceException e) {
            log.error("Error getting database size", e);
            throw new DataAccessException("Error getting database size", e);
        }
    }
}