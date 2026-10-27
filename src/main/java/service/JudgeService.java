package service;

import static model.SubmissionStatusEnum.*;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import model.Problem;
import model.Submission;
import model.TestCase;
import repositoryDataBase.ProblemDataBaseRepository;
import repositoryDataBase.SubmissionDataBaseRepository;
import service.codeRunner.CompilationResult;
import service.codeRunner.ExecutionResult;
import service.codeRunner.LanguageRunner;
import service.codeRunner.LanguageRunnerFactory;

@Singleton
public class JudgeService {

  public SubmissionDataBaseRepository submissionDataBaseRepository;
  public SubmissionService submissionService;
  public PlagiarismService plagiarismService;
  public IProblemService problemService;
  public LanguageRunnerFactory runnerFactory;

  @Inject
  public JudgeService(
      SubmissionDataBaseRepository submissionDataBaseRepository,
      SubmissionService submissionService,
      PlagiarismService plagiarismService,
      IProblemService problemService,
      LanguageRunnerFactory runnerFactory) {
    this.submissionDataBaseRepository = submissionDataBaseRepository;
    this.submissionService = submissionService;
    this.plagiarismService = plagiarismService;
    this.problemService = problemService;
    this.runnerFactory = runnerFactory;
  }

  public void runHardCodedTest(String path) {
    try {
      String[] EXPECTED_OUTPUTS = {"300", "400"};
      String[] INPUT = {"100 200", "200 300"};
      File sourceFile = new File(path);
      File workingDirectory = sourceFile.getParentFile();
      String fileName = sourceFile.getName();
      String className = fileName.substring(0, fileName.lastIndexOf('.'));

      System.out.println("Compiling...");
      ProcessBuilder compiler = new ProcessBuilder("javac", fileName);
      compiler.directory(workingDirectory);
      Process compileProcess = compiler.start();
      String compilationErrors =
          new String(compileProcess.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

      int exitCode = compileProcess.waitFor();
      if (exitCode != 0) {

        System.out.println("Compilation error:\n" + compilationErrors);

        return;
      }

      int tests = 0;
      for (String testCase : INPUT) {
        System.out.println("Running...");
        ProcessBuilder runner = new ProcessBuilder("java", className);
        runner.directory(workingDirectory);
        Process runProcess = runner.start();
        BufferedWriter stdin =
            new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream()));
        //          OutputStream stdin = runProcess.getOutputStream();

        stdin.write(testCase);
        stdin.flush();
        stdin.close();

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          output.append(line);
        }

        if (output.toString().equals(EXPECTED_OUTPUTS[tests])) {
          System.out.println("Test " + tests + " passed");
        } else {
          System.out.println("Test " + tests + " failed");
        }
        tests++;
        runProcess.waitFor();

        System.out.println("Finished.");
      }

    } catch (IOException e) {

    } catch (InterruptedException e) {

    } catch (Exception e) {

    }
  }

  public Submission submitProblem(UUID userId, UUID problemId, String pathToFolder, String language)
      throws IOException, InterruptedException {

    Problem problem = problemService.getProblemById(problemId);
    try {
      File sourceFile = new File(pathToFolder);
      if (!sourceFile.exists() && pathToFolder.startsWith("Users/")) {
        sourceFile = new File("/" + pathToFolder.trim());
      }

      if (!sourceFile.exists() || !sourceFile.isFile()) {
        System.out.println("[Error] File not found: " + pathToFolder);
        return null;
      }

      File workingDirectory = sourceFile.getParentFile();
      String fileName = sourceFile.getName();
      String className = fileName.substring(0, fileName.lastIndexOf('.'));

      LanguageRunner runner = runnerFactory.getRunner(language);

      Submission submission =
          submissionService.createSubmission(
              userId,
              problemId,
              Files.readString(sourceFile.toPath()),
              LocalDateTime.now(),
              language);

      System.out.println("Compiling...");
      submission.setStatus(COMPILING);
      submissionDataBaseRepository.save(submission);

      CompilationResult compilationResult =
          runner.compile(sourceFile.getAbsolutePath(), workingDirectory.getAbsolutePath());
      if (!compilationResult.isSuccess()) {
        submission.setStatus(COMPILATION_ERROR);
        submission.setErrorMessage(compilationResult.getErrorMessage());
        submissionDataBaseRepository.save(submission);
        System.out.println("Compilation error: " + compilationResult.getErrorMessage());
        return submission;
      }
      //
      //      ProcessBuilder compiler = new ProcessBuilder("javac", fileName);
      //
      //      compiler.directory(workingDirectory);
      //      Process compileProcess = compiler.start();
      //      String compilationErrors =
      //          new String(compileProcess.getErrorStream().readAllBytes(),
      // StandardCharsets.UTF_8);

      //      int exitCode = compileProcess.waitFor();
      //      if (exitCode != 0) {
      //        submission.setStatus(COMPILATION_ERROR);
      //        submission.setErrorMessage(compilationErrors);
      //        submissionDataBaseRepository.save(submission);
      //        System.out.println("Compilation error: " + compilationErrors);
      //        return submission;
      //      }

      List<TestCase> tests = problem.getTestCases();
      int testCounter = 0;
      int correctCounter = 0;
      int wrongCounter = 0;

      for (TestCase testCase : tests) {
        System.out.println(testCase.getInput() + " and " + testCase.getExpectedOutput());
        System.out.println("Running...");
        submission.setStatus(RUNNING_TESTS);

        ExecutionResult execResult =
            runner.execute(workingDirectory.getAbsolutePath(), testCase.getInput(), 2000L);

        if (execResult.isTimedOut()) {
          submission.setStatus(TIME_LIMIT_EXCEEDED);
          submissionDataBaseRepository.save(submission);
          return submission;
        }

        if (execResult.getExitCode() != 0) {
          submission.setStatus(RUNTIME_ERROR);
          submission.setErrorMessage(execResult.getErrorOutput());
          submissionDataBaseRepository.save(submission);
          return submission;
        }

        //        ProcessBuilder runner = new ProcessBuilder("java", className);
        //        runner.directory(workingDirectory);
        //        Process runProcess = runner.start();
        //        BufferedWriter stdin =
        //            new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream()));
        //        //          OutputStream stdin = runProcess.getOutputStream();
        //
        //        stdin.write(testCase.getInput());
        //        stdin.flush();
        //        stdin.close();

        //        BufferedReader reader =
        //            new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
        //        StringBuilder output = new StringBuilder();
        //        String line;
        //        while ((line = reader.readLine()) != null) {
        //          output.append(line);
        //        }

        String actualOutput = execResult.getOutput().trim();
        String expectedOutput = testCase.getExpectedOutput().trim();

        if (actualOutput.equals(expectedOutput)) {
          System.out.println("Test " + testCounter + " passed");
          correctCounter++;
        } else {
          System.out.println("Test " + testCounter + " failed");
          wrongCounter++;
        }
        testCounter++;
        System.out.println("Finished test.");
      }

      if (correctCounter == testCounter) {
        submission.setStatus(ACCEPTED);
        List<Submission> submissions =
            submissionDataBaseRepository.getSubmissionsOfProblem(problemId);
        for (Submission pastSubmission : submissions) {
          if (pastSubmission.equals(submission)) continue;
          if (pastSubmission.getStatus() == ACCEPTED) {
            double score =
                plagiarismService.calculateSimilarity(
                    pastSubmission.getSourceCode(), submission.getSourceCode());
            if (score > 0.8) {
              submission.setStatus(PLAGIARISM_FLAGGED);
              System.out.println("Submission found with plagiarism with score: " + score);
              break;
            }
          }
        }

      } else submission.setStatus(WRONG_ANSWER);
      submissionDataBaseRepository.save(submission);
      System.out.println("Testing finished");
      System.out.println("Correct answers: " + correctCounter);
      System.out.println("Wrong answers: " + wrongCounter);
      return submission;

    } catch (Exception e) {
      System.out.println("[Exception]" + e.getMessage());
    }
    return null;
  }

  public void judgeSubmissionById(UUID submissionId) throws IOException {

    Submission submission = submissionService.getSubmissionById(submissionId);
    if (submission == null) {
      System.out.println("[Judge] Submission not found " + submissionId);
      return;
    }

    Problem problem = problemService.getProblemById(submission.getProblemId());
    if (problem == null) {
      System.out.println("[Judge] Problem not found " + submission.getProblemId());
      return;
    }

    String language = submission.getLanguage() != null ? submission.getLanguage() : "java";

    LanguageRunner runner;
    try {
      runner = runnerFactory.getRunner(language);
    } catch (IllegalArgumentException e) {
      System.out.println("[Judge] " + e.getMessage());
      submission.setStatus(COMPILATION_ERROR);
      submission.setErrorMessage("Unsupported language: " + language);
      submissionDataBaseRepository.save(submission);
      return;
    }

    Path tempDir = null;

    try {
      tempDir = Files.createTempDirectory("judge_exec_" + submissionId + "_");

      String fileName = getFileNameForLanguage(language);
      Path sourceFilePath = tempDir.resolve(fileName);
      Files.writeString(sourceFilePath, submission.getSourceCode());

      System.out.println("Compiling...");
      submission.setStatus(COMPILING);
      submissionDataBaseRepository.save(submission);

      CompilationResult compilationResult =
          runner.compile(sourceFilePath.toString(), tempDir.toString());

      if (!compilationResult.isSuccess()) {
        submission.setStatus(COMPILATION_ERROR);
        submission.setErrorMessage(compilationResult.getErrorMessage());
        submissionDataBaseRepository.save(submission);
        System.out.println("Compilation error: " + compilationResult.getErrorMessage());
        return;
      }

      List<TestCase> tests = problem.getTestCases();
      int testCounter = 0;
      int correctCounter = 0;
      int wrongCounter = 0;

      for (TestCase testCase : tests) {
        System.out.println(testCase.getInput() + " and " + testCase.getExpectedOutput());
        System.out.println("Running...");
        submission.setStatus(RUNNING_TESTS);
        submissionDataBaseRepository.save(submission);

        ExecutionResult execResult =
            runner.execute(tempDir.toString(), testCase.getInput(), problem.getTimeLimitMs());

        if (execResult.isTimedOut()) {
          submission.setStatus(TIME_LIMIT_EXCEEDED);
          submission.setErrorMessage("Time Limit Exceeded on test " + testCounter);
          submissionDataBaseRepository.save(submission);
          System.out.println("Time Limit Exceeded on test " + testCounter);
          return;
        }

        if (execResult.getExitCode() != 0) {
          submission.setStatus(RUNTIME_ERROR);
          submission.setErrorMessage(execResult.getErrorOutput());
          submissionDataBaseRepository.save(submission);
          System.out.println(
              "Runtime error on test " + testCounter + ": " + execResult.getErrorOutput());
          return;
        }

        String actualOutput = execResult.getOutput().trim();
        String expectedOutput = testCase.getExpectedOutput().trim();

        if (actualOutput.equals(expectedOutput)) {
          System.out.println("Test " + testCounter + " passed");
          correctCounter++;
        } else {
          System.out.println("Test " + testCounter + " failed");
          wrongCounter++;
        }
        testCounter++;

        System.out.println("Finished test.");
      }

      if (correctCounter == testCounter) {
        submission.setStatus(ACCEPTED);
        List<Submission> submissions =
            submissionDataBaseRepository.getSubmissionsOfProblem(problem.getId());
        for (Submission pastSubmission : submissions) {
          if (pastSubmission.equals(submission)) continue;
          if (pastSubmission.getStatus() == ACCEPTED) {
            double score =
                plagiarismService.calculateSimilarity(
                    pastSubmission.getSourceCode(), submission.getSourceCode());
            if (score > 0.8) {
              submission.setStatus(PLAGIARISM_FLAGGED);
              System.out.println("Submission found with plagiarism with score: " + score);
              break;
            }
          }
        }
      } else {
        submission.setStatus(WRONG_ANSWER);
      }

      submissionDataBaseRepository.save(submission);
      System.out.println("Testing finished");
      System.out.println("Correct answers: " + correctCounter);
      System.out.println("Wrong answers: " + wrongCounter);

    } catch (Exception e) {
      System.out.println("[Exception] " + e.getMessage());
    } finally {
      if (tempDir != null) {
        try {
          Files.walk(tempDir)
              .sorted(Comparator.reverseOrder())
              .map(Path::toFile)
              .forEach(File::delete);
        } catch (IOException e) {
          // empty
        }
      }
    }
  }

  // Helper method to determine the source file name based on language
  private String getFileNameForLanguage(String language) {
    return switch (language.toLowerCase()) {
      case "java" -> "Solution.java";
      case "cpp", "c++" -> "solution.cpp";
      default ->
          throw new IllegalArgumentException("Unsupported language extension for: " + language);
    };
  }
}
