import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryTest {
    @BeforeEach
    public void fileCleanUp(){
        Path path = Path.of("json_folder/users.json");

        try {
            // Opening with TRUNCATE_EXISTING empties the file immediately
            Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING).close();
        } catch (IOException e) {
            System.err.println("Failed to clear file: " + e.getMessage());
        }
    }
    @Test
    public void testFindById() {
        UserRepository userRepository = new UserRepository();
        User user = new User("andrei1", "email");
        userRepository.save(user);
        UUID id = user.getId();
        Optional<User> foundUser = userRepository.findById(id);

        Assertions.assertEquals(Optional.of(user), foundUser);
    }

    @Test
    public void testGetUserByUsername(){
        UserRepository userRepository = new UserRepository();
        User user = new User("andrei2", "email");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.getUserByUsername(user.getUsername());
        Assertions.assertEquals(Optional.of(user), foundUser);

    }

    @Test
    public void testFindAll() {
        UserRepository userRepository = new UserRepository();
        User user = new User("andrei3", "email");
        userRepository.save(user);
        List<User> userList = userRepository.findAll();
        Assertions.assertNotNull(userList);
    }

    @Test
    public void testDeleteUserById() throws IOException {
        UserRepository userRepository = new UserRepository();
        User user = new User("andrei4", "email");
        userRepository.save(user);

        Assertions.assertTrue(userRepository.deleteById(user.getId()));

    }




}
