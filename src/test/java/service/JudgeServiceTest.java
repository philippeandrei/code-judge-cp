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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import repositoryDataBase.SubmissionDataBaseRepository;
import service.codeRunner.LanguageRunnerFactory;

@ExtendWith(MockitoExtension.class)
class JudgeServiceTest {
  @Mock private SubmissionDataBaseRepository submissionDataBaseRepository;
  @Mock private SubmissionService submissionService;
  @Mock private PlagiarismService plagiarismService;
  @Mock private ProblemService problemService;

  @Spy private LanguageRunnerFactory runnerFactory = new
          LanguageRunnerFactory();

  @InjectMocks private JudgeService sut;

  @Test
  void testSubmitProblem_Java(@TempDir Path tempDir) throws Exception {
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

  @Test
  void testSubmitProblem_Java_CompilationError(@TempDir Path tempDir) throws Exception {
    UUID userId = UUID.randomUUID();
    UUID problemId = UUID.randomUUID();
    Path file = tempDir.resolve("Solution.java");
    Files.writeString(
            file,
            """
            public class Solution { 
                public static void main(String[] args) { 
                    int a = 5  // Syntax error: missing semicolon here
                    System.out.println(a); 
                }
            }
            """);

    Problem problem = new Problem();
    problem.setId(problemId);
    String language = "java";

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
    assertEquals(SubmissionStatusEnum.COMPILATION_ERROR, submissionAnswer.getStatus());
  }

  @Test
  void testJudgeSubmissionById_Cpp() throws Exception {
    UUID submissionId = UUID.randomUUID();
    UUID problemId = UUID.randomUUID();


    Submission submission = new Submission();
    submission.setId(submissionId);
    submission.setProblemId(problemId);
    submission.setLanguage("java");
    submission.setSourceCode("""
            import java.util.Scanner;
            public class Solution {
                public static void main(String[] args) {
                    Scanner sc = new Scanner(System.in);
                    int a = sc.nextInt();
                    int b = sc.nextInt();
                    System.out.println(a + b);
                }
            }
            """);

    Problem problem = new Problem();
    problem.setId(problemId);
    problem.setTimeLimitMs(2000L);
    problem.setTestCases(List.of(
            new TestCase("1 2", "3"),
            new TestCase("10 20", "30")
    ));
    when(submissionService.getSubmissionById(submissionId)).
            thenReturn(submission);
    when(problemService.getProblemById(problemId)).thenReturn(problem);

    // When
    sut.judgeSubmissionById(submissionId);

    // Then
    assertEquals(SubmissionStatusEnum.ACCEPTED, submission.getStatus());
  }

}
