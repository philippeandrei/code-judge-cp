package repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository <T, ID>{
    T save(T element);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean deleteById(ID id);
}
