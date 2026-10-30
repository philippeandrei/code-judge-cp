package service;

import model.Category;
import model.Problem;
import model.TestCase;

import java.util.List;
import java.util.UUID;

public interface IProblemService {
    void createProblem(String title, String statement, long timeLimitMs, long memoryLimitKb);
    Problem createProblem(String title, String statement, long timeLimitMs, long memoryLimitKb, List<TestCase> testCases, List<Category> categories);
    Problem getProblemById(UUID id);
    Problem getProblemByTitle(String title);
    void deleteProblemById(UUID id);
    void addTestCaseToProblem(UUID problemId, String input, String expectedOutput);
    List<Problem> getAllProblems();


}
