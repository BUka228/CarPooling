package com.carpooling.dao.xml;

import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.record.UserRecord;
import com.carpooling.exceptions.dao.DataAccessException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Optional;

import static com.carpooling.constants.ErrorMessages.USER_UPDATE_ERROR;
import static com.carpooling.constants.ErrorMessages.*;
import static com.carpooling.constants.LogMessages.*;


@Slf4j
public class XmlUserDao extends AbstractXmlDao<UserRecord, XmlUserDao.UserWrapper> implements UserDao {

    public XmlUserDao(String filePath) {
        super(UserRecord.class, UserWrapper.class, filePath);
    }

    @Override
    public String createUser(@NotNull UserRecord userRecord) throws DataAccessException {
        log.info(CREATE_USER_START, userRecord.getName());

        // Генерация UUID для ID
        String userId = generateId();
        userRecord.setId(userId);

        try {
            List<UserRecord> users = readAll();
            users.add(userRecord);
            writeAll(users);

            log.info(CREATE_USER_SUCCESS, userId);
            return userId;
        } catch (JAXBException e) {
            log.error(ERROR_CREATE_USER, userRecord.getName(), e);
            throw new DataAccessException(USER_CREATION_ERROR, e);
        }
    }

    @Override
    public Optional<UserRecord> getUserById(String id) throws DataAccessException {
        log.info(GET_USER_START, id);
        try {
            return findById(record -> record.getId().equals(id));
        } catch (JAXBException e) {
            log.error(ERROR_GET_USER, id, e);
            throw new DataAccessException(String.format(USER_NOT_FOUND_ERROR, id), e);
        }
    }

    @Override
    public void updateUser(@NotNull UserRecord userRecord) throws DataAccessException {
        try {
            List<UserRecord> users = readAll();
            boolean updated = updateItem(users, record -> record.getId().equals(userRecord.getId()), userRecord);

            if (!updated) {
                log.warn(WARN_USER_NOT_FOUND, userRecord.getId());
                throw new DataAccessException(String.format(USER_NOT_FOUND_ERROR, userRecord.getId()));
            }

            writeAll(users);
            log.info(UPDATE_USER_SUCCESS, userRecord.getId());
        } catch (JAXBException e) {
            log.error(ERROR_UPDATE_USER, userRecord.getId(), e);
            throw new DataAccessException(USER_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteUser(String id) throws DataAccessException {
        try {
            deleteById(record -> record.getId().equals(id));
            log.info(DELETE_USER_SUCCESS, id);
        } catch (JAXBException e) {
            log.error(ERROR_DELETE_USER, id, e);
            throw new DataAccessException(USER_DELETE_ERROR, e);
        }
    }

    @Override
    protected List<UserRecord> getItemsFromWrapper(@NotNull UserWrapper wrapper) {
        return wrapper.getUsers();
    }

    @Override
    protected UserWrapper createWrapper(List<UserRecord> items) {
        return new UserWrapper(items);
    }

    /**
     * Вспомогательный класс для обертки списка пользователей в XML.
     */
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @XmlRootElement(name = "users")
    protected static class UserWrapper {
        private List<UserRecord> users;

        @XmlElement(name = "user")
        public List<UserRecord> getUsers() {
            return users;
        }
    }
}



