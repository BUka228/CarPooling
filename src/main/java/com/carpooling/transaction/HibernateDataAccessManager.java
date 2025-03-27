package com.carpooling.transaction;

import com.carpooling.exceptions.dao.DataAccessException;
import com.carpooling.hibernate.ThreadLocalSessionContext;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateDataAccessManager implements DataAccessManager {

    private static final Logger log = LoggerFactory.getLogger(HibernateDataAccessManager.class);
    private final SessionFactory sessionFactory;

    public HibernateDataAccessManager(SessionFactory sessionFactory) {
        if (sessionFactory == null) {
            throw new IllegalArgumentException("SessionFactory cannot be null for HibernateDataAccessManager");
        }
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <R> R executeInTransaction(DataAccessAction<R> action) throws DataAccessException {
        Session session = null;
        Transaction transaction = null;
        boolean sessionBound = false;
        try {
            session = sessionFactory.openSession();
            ThreadLocalSessionContext.bind(session); // Связываем с потоком
            sessionBound = true;
            transaction = session.beginTransaction(); // Начинаем транзакцию
            log.debug("Hibernate transaction started: {}", session);
            try {
                R result = action.execute(); // Выполняем действие
                transaction.commit();       // Коммитим
                log.debug("Hibernate transaction committed.");
                return result;
            } catch (Exception e) {
                log.error("Exception during transactional action, rolling back.", e);
                rollbackTransaction(transaction, e); // Откатываем
                throw wrapException(e, true); // Оборачиваем ошибку
            }
        } catch (HibernateException e) {
            log.error("HibernateException during session/transaction management", e);
            throw new DataAccessException("Error managing Hibernate session/transaction", e);
        } catch (DataAccessException e) { // Если wrapException вернул DataAccessException
            throw e;
        } catch (Exception e) { // Другие ошибки (например, из action, не пойманные wrapException)
            log.error("Unexpected error during transaction execution", e);
            throw new DataAccessException("Unexpected transactional error", e);
        } finally {
            // Отвязываем и закрываем в finally
            unbindAndCloseSession(session, sessionBound);
        }
    }

    @Override
    public <R> R executeReadOnly(DataAccessAction<R> action) throws DataAccessException {
        Session session = null;
        boolean sessionBound = false;
        try {
            session = sessionFactory.openSession();
            ThreadLocalSessionContext.bind(session); // Связываем с потоком
            sessionBound = true;
            log.debug("Session opened and bound for read-only operation: {}", session);
            try {
                // НЕТ session.beginTransaction()
                R result = action.execute(); // Выполняем действие
                log.debug("Read-only action executed successfully.");
                return result;
            } catch (Exception e) {
                log.error("Exception during read-only action execution.", e);
                throw wrapException(e, false); // Оборачиваем ошибку
            }
        } catch (HibernateException e) {
            log.error("HibernateException during read-only session management", e);
            throw new DataAccessException("Error managing Hibernate session for read", e);
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during read-only execution", e);
            throw new DataAccessException("Unexpected read-only error", e);
        } finally {
            // Отвязываем и закрываем в finally
            unbindAndCloseSession(session, sessionBound);
        }
    }

    // --- Вспомогательные методы ---

    private void rollbackTransaction(Transaction transaction, Exception originalException) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
                log.warn("Hibernate transaction rolled back due to exception.");
            } catch (HibernateException rbEx) {
                log.error("Could not rollback transaction after exception!", rbEx);
                if(originalException != null) originalException.addSuppressed(rbEx);
            }
        }
    }

    private void unbindAndCloseSession(Session session, boolean sessionBound) {
        Session unboundSession = null;
        if (sessionBound) {
            // Отвязываем от потока
            unboundSession = ThreadLocalSessionContext.unbind();
        }
        // Закрываем сессию, которую МЫ открыли (если она есть и открыта)
        if (session != null && session.isOpen()) {
            try {
                session.close();
                log.debug("Hibernate session closed: {}", session);
            } catch (HibernateException e) {
                log.error("Error closing Hibernate session: {}", session, e);
            }
        }
        // Дополнительная проверка на всякий случай, если unbind вернул другую сессию
        if (sessionBound && unboundSession != null && unboundSession != session && unboundSession.isOpen()) {
            log.warn("Unbound session {} differs from the session that was explicitly closed {}. Closing unbound session too.", unboundSession, session);
            try {
                unboundSession.close();
            } catch (HibernateException e) {
                log.error("Error closing unbound Hibernate session: {}", unboundSession, e);
            }
        }
    }

    private DataAccessException wrapException(Exception e, boolean inTransaction) {
        String context = inTransaction ? "within transaction" : "during read operation";
        if (e instanceof DataAccessException) return (DataAccessException) e;
        if (e instanceof HibernateException) return new DataAccessException("Hibernate operation failed " + context, e);
        return new DataAccessException("Unexpected error " + context, e);
    }
}