import model.Problem;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.ProblemRepository;
import repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProblemRepositoryTest {
    @BeforeEach
    public void fileCleanUp(){
        Path path = Path.of("json_folder/problems.json");

        try {
            // Opening with TRUNCATE_EXISTING empties the file immediately
            Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING).close();
        } catch (IOException e) {
            System.err.println("Failed to clear file: " + e.getMessage());
        }
    }

    @Test
    public void testFindById() {
        ProblemRepository problemRepository = new ProblemRepository();
        //String title, String statement, long timeLimitMs, long memoryLimitKb
        Problem problem = new Problem("My problem1", "Write a c++ program that...", 2000, 100);
        problemRepository.save(problem);
        UUID id = problem.getId();
        Optional<Problem> foundProblem = problemRepository.findById(id);

        Assertions.assertEquals(Optional.of(problem), foundProblem);
    }

    @Test
    public void testGetProblemByUsername(){
        ProblemRepository problemRepository = new ProblemRepository();
        //String title, String statement, long timeLimitMs, long memoryLimitKb
        Problem problem = new Problem("My problem2", "Write a c++ program that...", 2000, 100);
        problemRepository.save(problem);

        Optional<Problem> foundProblem = problemRepository.getProblemByTitle(problem.getTitle());
        Assertions.assertEquals(Optional.of(problem), foundProblem);

    }

    @Test
    public void testFindAll() {
        UserRepository userRepository = new UserRepository();
        ProblemRepository problemRepository = new ProblemRepository();
        //String title, String statement, long timeLimitMs, long memoryLimitKb
        Problem problem = new Problem("My problem2", "Write a c++ program that...", 2000, 100);
        problemRepository.save(problem);

        List<User> userList = userRepository.findAll();
        Assertions.assertNotNull(userList);
    }

    @Test
    public void testDeleteUserById() throws IOException {
        ProblemRepository problemRepository = new ProblemRepository();
        //String title, String statement, long timeLimitMs, long memoryLimitKb
        Problem problem = new Problem("My problem3", "Write a c++ program that...", 2000, 100);
        problemRepository.save(problem);


        Assertions.assertTrue(problemRepository.deleteById(problem.getId()));

    }



}
