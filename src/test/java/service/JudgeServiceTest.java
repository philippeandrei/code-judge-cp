package service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import model.Problem;
import model.Submission;
import model.SubmissionStatusEnum;
import model.TestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repositoryDataBase.SubmissionDataBaseRepository;

@ExtendWith(MockitoExtension.class)
class JudgeServiceTest {
  @Mock private SubmissionDataBaseRepository submissionDataBaseRepository;
  @Mock private SubmissionService submissionService;
  @Mock private PlagiarismService plagiarismService;
  @Mock private IProblemService problemService;

  @InjectMocks private JudgeService sut;

  @Test
  void testSubmitProblem_(@TempDir Path tempDir) throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    UUID problemId = UUID.randomUUID();
    Path file = tempDir.resolve("Solution.java");
    Files.writeString(
        file,
"""
 import java.util.Scanner;\\n\\npublic class Solution {\\npublic static void main(String[] args) {\\n        Scanner sc = new Scanner(System.in);\\n        int a = sc.nextInt();\\n        int b = sc.nextInt();\\n        System.out.println(a + b);\\n    }\\n}
""");

    Problem problem = new Problem();
    problem.setTestCases(
        List.of(new TestCase("1 2", "3"), new TestCase("3 4", "7"), new TestCase("2 444", "446")));
    when(problemService.getProblemById(problem.getId())).thenReturn(problem);

    // When
    Submission submission = sut.submitProblem(userId, problemId, file.toString());

    // Then
    assertEquals(SubmissionStatusEnum.ACCEPTED, submission.getStatus());
  }
}
