package relations.queries;

import com.carpooling.exceptions.dao.DataAccessException; // Предполагаем, что этот класс существует
import java.util.List;

public interface SummaryQueryDao {

    /**
     * Получает количество поездок для каждого пользователя с использованием HQL.
     * @return Список DTO с именем пользователя и количеством его поездок.
     * @throws DataAccessException если возникает ошибка доступа к данным.
     */
    List<UserTripCountDto> getUserTripCountsHql() throws DataAccessException;

    /**
     * Получает количество поездок для каждого пользователя с использованием Criteria API.
     * @return Список DTO с именем пользователя и количеством его поездок.
     * @throws DataAccessException если возникает ошибка доступа к данным.
     */
    List<UserTripCountDto> getUserTripCountsCriteria() throws DataAccessException;

    /**
     * Получает количество поездок для каждого пользователя с использованием Native SQL.
     * @return Список DTO с именем пользователя и количеством его поездок.
     * @throws DataAccessException если возникает ошибка доступа к данным.
     */
    List<UserTripCountDto> getUserTripCountsNativeSql() throws DataAccessException;
}