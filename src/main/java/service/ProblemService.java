package service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import exception.ProblemNotFoundException;
import java.util.List;
import java.util.UUID;
import model.Category;
import model.Problem;
import model.TestCase;
import repositoryDataBase.ProblemDataBaseRepository;

@Singleton
public class ProblemService implements IProblemService {
  public ProblemDataBaseRepository problemDataBaseRepository;

  @Inject
  public ProblemService(ProblemDataBaseRepository problemDataBaseRepository) {
    this.problemDataBaseRepository = problemDataBaseRepository;
  }

  // String title, String statement, long timeLimitMs, long memoryLimitKb) {
  public void createProblem(String title, String statement, long timeLimitMs, long memoryLimitKb) {
    Problem problem = new Problem(title, statement, timeLimitMs, memoryLimitKb);
    problemDataBaseRepository.save(problem);
  }

  public Problem createProblem(
      String title,
      String statement,
      long timeLimitMs,
      long memoryLimitKb,
      List<TestCase> testCases,
      List<Category> categories) {
    Problem problem =
        new Problem(title, statement, timeLimitMs, memoryLimitKb, testCases, categories);
    problemDataBaseRepository.save(problem);
    return problem;
  }

  public Problem getProblemById(UUID id) {
    return problemDataBaseRepository
        .findById(id)
        .orElseThrow(
            () -> new ProblemNotFoundException("Can not find any problem with the id: " + id));
  }

  public Problem getProblemByTitle(String title) {
    return problemDataBaseRepository
        .getProblemByTitle(title)
        .orElseThrow(
            () ->
                new ProblemNotFoundException("Can not find any problem with the title: " + title));
  }

  public void deleteProblemById(UUID id) {
    if (!problemDataBaseRepository.deleteById(id))
      throw new ProblemNotFoundException("Can not find any problem with id: " + id);
  }

  public void addTestCaseToProblem(UUID problemId, String input, String expectedOutput) {
    Problem problem = getProblemById(problemId);
    TestCase testCase = new TestCase(input, expectedOutput);

    problem.addTestCase(testCase);

    problemDataBaseRepository.save(problem);
  }

  public List<Problem> getAllProblems() {
    return problemDataBaseRepository.findAll();
  }
}
