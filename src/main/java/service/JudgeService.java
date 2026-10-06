package service;

import static model.SubmissionStatusEnum.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import model.Problem;
import model.Submission;
import model.TestCase;
import repository.ProblemRepository;
import repository.SubmissionRepository;

public class JudgeService {
  public SubmissionRepository submissionRepository = new SubmissionRepository();
  public SubmissionService submissionService = new SubmissionService(submissionRepository);
  public PlagiarismService plagiarismService = new PlagiarismService();

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

  public Submission submitProblem(UUID userId, UUID problemId, String pathToFolder)
      throws IOException, InterruptedException {
    ProblemService problemService = new ProblemService(new ProblemRepository());

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

      //    //    public Submission(UUID userId, UUID problemId, String sourceCode, LocalDateTime
      // submittedTime){
      Submission submission =
          submissionService.createSubmission(
              userId, problemId, Files.readString(sourceFile.toPath()), LocalDateTime.now());
      System.out.println("Compiling...");
      submission.setStatus(COMPILING);
      ProcessBuilder compiler = new ProcessBuilder("javac", fileName);

      compiler.directory(workingDirectory);
      Process compileProcess = compiler.start();
      String compilationErrors =
          new String(compileProcess.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

      int exitCode = compileProcess.waitFor();
      if (exitCode != 0) {
        submission.setStatus(COMPILATION_ERROR);
        submission.setErrorMessage(compilationErrors);
        submissionRepository.saveToFile();
        System.out.println("Compilation error: " + compilationErrors);
        return submission;
      }

      List<TestCase> tests = problem.getTestCases();
      int testCounter = 0;
      int correctCounter = 0;
      int wrongCounter = 0;
      for (TestCase testCase : tests) {
        System.out.println(testCase.getInput() + " and " + testCase.getExpectedOutput());
        System.out.println("Running...");
        submission.setStatus(RUNNING_TESTS);

        ProcessBuilder runner = new ProcessBuilder("java", className);
        runner.directory(workingDirectory);
        Process runProcess = runner.start();
        BufferedWriter stdin =
            new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream()));
        //          OutputStream stdin = runProcess.getOutputStream();

        stdin.write(testCase.getInput());
        stdin.flush();
        stdin.close();

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          output.append(line);
        }

        if (output.toString().equals(testCase.getExpectedOutput())) {
          System.out.println("Test " + testCounter + " passed");
          correctCounter++;
        } else {
          System.out.println("Test " + testCounter + " failed");
          wrongCounter++;
        }
        testCounter++;
        runProcess.waitFor();

        System.out.println("Finished test.");
      }
      if (correctCounter == testCounter) {
        submission.setStatus(ACCEPTED);
        List<Submission> submissions = submissionRepository.getSubmissionsOfProblem(problemId);
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
      submissionRepository.saveToFile();
      System.out.println("Testing finished");
      System.out.println("Correct answers: " + correctCounter);
      System.out.println("Wrong answers: " + wrongCounter);
      return submission;

    } catch (Exception e) {
      System.out.println("[Exception]" + e.getMessage());
    }
    return null;
  }
}
