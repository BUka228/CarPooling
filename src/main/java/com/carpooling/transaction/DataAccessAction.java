package com.carpooling.transaction;


@FunctionalInterface
public interface DataAccessAction<R> {
    /**
     * Выполняет действие с доступом к данным.
     * @return Результат действия.
     * @throws Exception Может бросать любые исключения, которые будут обработаны менеджером.
     */
    R execute() throws Exception;
}