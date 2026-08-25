import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import repository.UserRepository;

import java.io.IOException;
import java.util.List;

public class UserRepositoryTest {
    @Test
    public void testFindAll() throws IOException {
        UserRepository userRepository = new UserRepository();
        User user = new User("andrei", "email");
        userRepository.save(user);
        List<User> userList = userRepository.findAll();
        Assertions.assertNotNull(userList);
    }

    @Test
    public void testDeleteUserById() throws IOException {
        UserRepository userRepository = new UserRepository();
        User user = new User("andrei", "email");
        userRepository.save(user);

        Assertions.assertTrue(userRepository.deleteById(user.getId()));

    }


}
