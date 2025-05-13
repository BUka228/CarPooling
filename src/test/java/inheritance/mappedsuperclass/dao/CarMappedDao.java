package inheritance.mappedsuperclass.dao;

import inheritance.mappedsuperclass.model.CarMapped;
import java.util.List;
import java.util.Optional;

public interface CarMappedDao {
    CarMapped save(CarMapped car);
    Optional<CarMapped> findById(Long id);
    List<CarMapped> findAll();
    void deleteById(Long id);
    CarMapped update(CarMapped car); // Добавим метод update
}