package com.carpooling.transaction;

import com.carpooling.exceptions.dao.DataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class NoOpDataAccessManager implements DataAccessManager {


    @Override
    public <R> R executeInTransaction(DataAccessAction<R> action) throws DataAccessException {
        log.debug("Executing action via NoOpDataAccessManager (transaction).");
        return execute(action);
    }

    @Override
    public <R> R executeReadOnly(DataAccessAction<R> action) throws DataAccessException {
        log.debug("Executing action via NoOpDataAccessManager (read-only).");
        return execute(action);
    }

    // Общий метод выполнения для NoOp
    private <R> R execute(DataAccessAction<R> action) throws DataAccessException {
        try {
            return action.execute();
        } catch (Exception e) {
            log.error("Exception during NoOp execution: {}", e.getMessage(), e);
            if (e instanceof DataAccessException) throw (DataAccessException) e;
            throw new DataAccessException("Unexpected error during action execution", e);
        }
    }
}