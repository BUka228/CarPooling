package inheritance.mappedsuperclass.dao;

import inheritance.mappedsuperclass.model.MotorcycleMapped;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class PostgresMotorcycleMappedDao implements MotorcycleMappedDao {
    private final SessionFactory sessionFactory;

    public PostgresMotorcycleMappedDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public MotorcycleMapped save(MotorcycleMapped motorcycle) {
        getCurrentSession().persist(motorcycle);
        return motorcycle;
    }

    @Override
    public Optional<MotorcycleMapped> findById(Long id) {
        return Optional.ofNullable(getCurrentSession().get(MotorcycleMapped.class, id));
    }

    @Override
    public List<MotorcycleMapped> findAll() {
        return getCurrentSession().createQuery("FROM MotorcycleMapped", MotorcycleMapped.class).list();
    }

    @Override
    public MotorcycleMapped update(MotorcycleMapped motorcycle) {
        return getCurrentSession().merge(motorcycle);
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(moto -> getCurrentSession().remove(moto));
    }
}