package inheritance.mappedsuperclass.dao;

import inheritance.mappedsuperclass.model.CarMapped;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class PostgresCarMappedDao implements CarMappedDao {
    private final SessionFactory sessionFactory;

    public PostgresCarMappedDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public CarMapped save(CarMapped car) {
        getCurrentSession().persist(car);
        return car;
    }

    @Override
    public Optional<CarMapped> findById(Long id) {
        return Optional.ofNullable(getCurrentSession().get(CarMapped.class, id));
    }

    @Override
    public List<CarMapped> findAll() {
        return getCurrentSession().createQuery("FROM CarMapped", CarMapped.class).list();
    }

    @Override
    public CarMapped update(CarMapped car) {
        return getCurrentSession().merge(car);
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(car -> getCurrentSession().remove(car));
    }
}