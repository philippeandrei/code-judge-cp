package repository;

import model.User;

import java.sql.Array;
import java.util.*;

public class UserRepository implements CrudRepository<User, UUID> {
    HashMap<UUID, User> users;

    @Override
    public User save(User user) {
        users.put(user.getId(), user);  
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        User user = users.get(id);
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean deleteById(UUID uuid) {
        return (users.remove(uuid) != null);
    }

    public Optional<User> getUserByUsername(String username){
        for(User user: users.values()){
            if(Objects.equals(user.getUsername(), username)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

}
