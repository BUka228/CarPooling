package inheritance.mappedsuperclass.dao;

import inheritance.mappedsuperclass.model.MotorcycleMapped;
import java.util.List;
import java.util.Optional;

public interface MotorcycleMappedDao {
    MotorcycleMapped save(MotorcycleMapped motorcycle);
    Optional<MotorcycleMapped> findById(Long id);
    List<MotorcycleMapped> findAll();
    void deleteById(Long id);
    MotorcycleMapped update(MotorcycleMapped motorcycle); // Добавим метод update
}