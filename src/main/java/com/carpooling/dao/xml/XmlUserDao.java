package com.carpooling.dao.xml;

import com.carpooling.dao.base.UserDao;
import com.carpooling.entities.database.User;
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
import java.util.UUID;

@Slf4j
public class XmlUserDao extends AbstractXmlDao<User, XmlUserDao.UserWrapper> implements UserDao {

    public XmlUserDao(String filePath) {
        super(User.class, UserWrapper.class, filePath);
    }

    @Override
    public String createUser(@NotNull User user) throws DataAccessException {
        UUID userId = generateId();
        user.setId(userId);

        try {
            List<User> users = readAll();
            users.add(user);
            writeAll(users);
            log.info("User created successfully: {}", userId);
            return userId.toString();
        } catch (JAXBException e) {
            log.error("Error creating user: {}", e.getMessage());
            throw new DataAccessException("Error creating user", e);
        }
    }

    @Override
    public Optional<User> getUserById(String id) throws DataAccessException {
        try {
            Optional<User> user = findById(record -> record.getId().toString().equals(id));
            if (user.isPresent()) {
                log.info("User found: {}", id);
            } else {
                log.warn("User not found: {}", id);
            }
            return user;
        } catch (JAXBException e) {
            log.error("Error reading user: {}", e.getMessage());
            throw new DataAccessException("Error reading user", e);
        }
    }

    @Override
    public void updateUser(@NotNull User user) throws DataAccessException {
        try {
            List<User> users = readAll();
            boolean updated = updateItem(record -> record.getId().equals(user.getId()), user);
            if (!updated) {
                log.warn("User not found for update: {}", user.getId());
                throw new DataAccessException("User not found");
            }
            log.info("User updated successfully: {}", user.getId());
        } catch (JAXBException e) {
            log.error("Error updating user: {}", e.getMessage());
            throw new DataAccessException("Error updating user", e);
        }
    }

    @Override
    public void deleteUser(String id) throws DataAccessException {
        try {
            boolean removed = deleteById(record -> record.getId().toString().equals(id));
            if (removed) {
                log.info("User deleted successfully: {}", id);
            } else {
                log.warn("User not found for deletion: {}", id);
            }
        } catch (JAXBException e) {
            log.error("Error deleting user: {}", e.getMessage());
            throw new DataAccessException("Error deleting user", e);
        }
    }

    @Override
    protected List<User> getItemsFromWrapper(@NotNull UserWrapper wrapper) {
        return wrapper.getUsers();
    }

    @Override
    protected UserWrapper createWrapper(List<User> items) {
        return new UserWrapper(items);
    }

    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @XmlRootElement(name = "users")
    protected static class UserWrapper {
        private List<User> users;

        @XmlElement(name = "user")
        public List<User> getUsers() {
            return users;
        }
    }
}



