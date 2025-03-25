package dao.userdao;

import com.carpooling.dao.base.UserDao;
import com.carpooling.dao.postgres.PostgresUserDao;
import com.carpooling.entities.database.User;
import com.carpooling.exceptions.dao.DataAccessException;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;



public class PostgresUserDaoTest extends AbstractUserDaoTest {

    private SessionFactory sessionFactory;

    @Override
    protected UserDao createUserDao() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        return new PostgresUserDao(sessionFactory);
    }

    @Override
    protected void cleanUp() {
        // Закрываем SessionFactory после каждого теста
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}