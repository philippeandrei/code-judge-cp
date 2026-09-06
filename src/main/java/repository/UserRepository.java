package repository;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.DataAccessException;
import model.User;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UserRepository implements CrudRepository<User, UUID> {
    HashMap<UUID, User> users = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();
    private final String FILE_PATH =  "json_folder/users.json";
    public UserRepository() {
        loadFromFile();

    }
    public void loadFromFile(){
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return; //am creat fisierul ca nu exista
            }
            List<User> usersList = mapper.readValue(file, new TypeReference<List<User>>() {});

            for (User user : usersList) {
//                System.out.println("ID: " + user.getId() + " | Username: " + user.getUsername());
                users.put(user.getId(), user);
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to load users from " + FILE_PATH, e);
        }
    }

    public void saveToFile()  {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), users.values());
        } catch (IOException e) {
            throw new DataAccessException("Failed to save data to " + FILE_PATH, e);
        }
    }


    @Override
    public User save(User user)  {
        users.put(user.getId(), user);
        saveToFile();
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
    public boolean deleteById(UUID uuid)  {

        User user = users.remove(uuid);
        if(user != null){
            saveToFile();
            return true;
        }
        return false;
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
