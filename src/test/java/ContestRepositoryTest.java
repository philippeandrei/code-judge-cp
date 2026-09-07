import model.Contest;
import model.Problem;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.ContestRepository;
import repository.ProblemRepository;
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

public class ContestRepositoryTest {
    @BeforeEach
    public void fileCleanUp(){
        Path path = Path.of("json_folder/contests.json");

        try {
            // Opening with TRUNCATE_EXISTING empties the file immediately
            Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING).close();
        } catch (IOException e) {
            System.err.println("Failed to clear file: " + e.getMessage());
        }
    }

    @Test
    public void testFindById() {
        ContestRepository contestRepository = new ContestRepository();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse("2026-10-10 10:10", formatter);
        LocalDateTime endTime = LocalDateTime.parse("2026-10-15 10:10", formatter);

        Contest contest = new Contest("My contest", startTime, endTime );

        contestRepository.save(contest);


        UUID id = contest.getId();
        Optional<Contest> foundContest = contestRepository.findById(id);

        Assertions.assertEquals(Optional.of(contest), foundContest);
    }

    @Test
    public void testGetContestByName(){
        ContestRepository contestRepository = new ContestRepository();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse("2026-10-10 10:10", formatter);
        LocalDateTime endTime = LocalDateTime.parse("2026-10-15 10:10", formatter);

        Contest contest = new Contest("My contest", startTime, endTime );

        contestRepository.save(contest);
        Optional<Contest> foundContest = contestRepository.getContestByName(contest.getName());
        Assertions.assertEquals(Optional.of(contest), foundContest);

    }

    @Test
    public void testFindAll() {
        ContestRepository contestRepository = new ContestRepository();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse("2026-10-10 10:10", formatter);
        LocalDateTime endTime = LocalDateTime.parse("2026-10-15 10:10", formatter);

        Contest contest = new Contest("My contest", startTime, endTime );

        contestRepository.save(contest);
        List<Contest> contestList = contestRepository.findAll();
        Assertions.assertNotNull(contestList);
    }

    @Test
    public void testDeleteContestById() throws IOException {
        ContestRepository contestRepository = new ContestRepository();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse("2026-10-10 10:10", formatter);
        LocalDateTime endTime = LocalDateTime.parse("2026-10-15 10:10", formatter);

        Contest contest = new Contest("My contest", startTime, endTime );

        contestRepository.save(contest);

        Assertions.assertTrue(contestRepository.deleteById(contest.getId()));

    }

}
