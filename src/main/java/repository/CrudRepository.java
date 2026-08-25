package repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CrudRepository <T, ID>{
    T save(T element) throws IOException;
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean deleteById(ID id) throws IOException;
}
