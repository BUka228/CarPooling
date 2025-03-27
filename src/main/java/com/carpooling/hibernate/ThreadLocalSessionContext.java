package com.carpooling.hibernate;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Простая реализация контекста сессии на основе ThreadLocal
@Slf4j
public class ThreadLocalSessionContext implements CurrentSessionContext {

    // ThreadLocal для хранения сессии для каждого потока
    private static final ThreadLocal<Session> context = new ThreadLocal<>();
    private static SessionFactory factory;

    /**
     * Конструктор, вызываемый Hibernate.
     * Принимает SessionFactoryImplementor согласно контракту CurrentSessionContext.
     * @param factory SessionFactoryImplementor, к которой привязан контекст.
     */
    public ThreadLocalSessionContext(SessionFactoryImplementor factory) {
        log.debug("ThreadLocalSessionContext created for SessionFactory: {}", factory);
    }
    /**
     * Возвращает сессию, связанную с текущим потоком.
     * @return Текущая сессия.
     * @throws HibernateException Если сессия не связана с потоком внешним менеджером.
     */
    @Override
    public @NotNull Session currentSession() throws HibernateException {
        Session session = context.get();
        // Важно: Мы НЕ открываем сессию здесь. Мы ожидаем, что она была открыта
        // и связана с потоком ИЗВНЕ (например, в HibernateTransactionManager).
        if (session == null) {
            throw new HibernateException("No Hibernate Session bound to thread. " +
                    "A transaction manager should bind/unbind sessions.");
        }
        if (!session.isOpen()) {
            log.warn("Session found in ThreadLocal is closed!");
            // Можно либо бросить исключение, либо удалить из ThreadLocal и бросить "No session"
            context.remove();
            throw new HibernateException("Session bound to thread is closed.");
        }
        log.trace("Returning session bound to thread: {}", session);
        return session;
    }

    /**
     * Связывает предоставленную сессию с текущим потоком.
     * Вызывается внешним менеджером транзакций ПЕРЕД началом работы с DAO.
     * @param session Сессия для связывания.
     */
    public static void bind(Session session) {
        if (session != null) {
            log.debug("Binding session to ThreadLocal: {}", session);
            if (context.get() != null && context.get() != session) {
                log.warn("Replacing existing session in ThreadLocal! Old: {}, New: {}", context.get(), session);
            }
            context.set(session);
        } else {
            log.warn("Attempted to bind null session to ThreadLocal.");
        }
    }

    /**
     * Отвязывает сессию от текущего потока.
     * Вызывается внешним менеджером транзакций ПОСЛЕ commit/rollback.
     * @return Отвязанная сессия (для последующего закрытия).
     */
    public static Session unbind() {
        Session session = context.get();
        context.remove(); // Всегда удаляем из ThreadLocal
        if (session != null) {
            log.debug("Unbinding session from ThreadLocal: {}", session);
        } else {
            log.trace("No session found in ThreadLocal to unbind.");
        }
        return session; // Возвращаем для закрытия
    }
}