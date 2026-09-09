import model.Submission;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.SubmissionRepository;
import repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SubmissionRepositoryTest {
    @BeforeEach
    public void fileCleanUp(){
        Path path = Path.of("json_folder/submissions.json");

        try {
            // Opening with TRUNCATE_EXISTING empties the file immediately
            Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING).close();
        } catch (IOException e) {
            System.err.println("Failed to clear file: " + e.getMessage());
        }
    }
    @Test
    public void testFindById() {
        SubmissionRepository submissionRepository = new SubmissionRepository();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime submittedTime = LocalDateTime.parse("2026-10-10 10:10", formatter);

        Submission submission = new Submission(UUID.randomUUID(), UUID.randomUUID(), "c++", submittedTime);
        submissionRepository.save(submission);


        UUID id = submission.getId();
        Optional<Submission> foundSubmission = submissionRepository.findById(id);

        Assertions.assertEquals(Optional.of(submission), foundSubmission);
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
