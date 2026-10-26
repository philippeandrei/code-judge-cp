package service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
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
 import java.util.Scanner; public class Solution { public static void main(String[] args) {         Scanner sc = new Scanner(System.in);         int a = sc.nextInt();        int b = sc.nextInt();        System.out.println(a + b);    }}
""");


    Problem problem = new Problem();
    problem.setId(problemId);
    String language = "java";
    problem.setTestCases(
        List.of(new TestCase("1 2", "3"), new TestCase("3 4", "7"), new TestCase("2 444", "446")));
    when(problemService.getProblemById(problemId)).thenReturn(problem);
    when(submissionService.createSubmission(any(), any(), any(), any(), any()))
        .thenAnswer(
            invocation ->
                new Submission(
                    invocation.getArgument(0),
                    invocation.getArgument(1),
                    invocation.getArgument(2),
                    invocation.getArgument(3),
                    "",
                        invocation.getArgument(4)
                        ));

    // When
    Submission submissionAnswer = sut.submitProblem(userId, problemId, file.toString(), language);

    // Then
    assertEquals(SubmissionStatusEnum.ACCEPTED, submissionAnswer.getStatus());
  }
}
