package dto;

import java.util.List;
import java.util.UUID;
import model.Category;
import model.Problem;
import model.TestCase;

public class ProblemResponseDto {
  private UUID id;
  private String title;
  private String statement;
  private long timeLimitMs;
  private long memoryLimitKb;
  private List<TestCase> testCases;
  private List<Category> categories;

  public ProblemResponseDto() {}

  public ProblemResponseDto(
      UUID id,
      String title,
      String statement,
      long timeLimitMs,
      long memoryLimitKb,
      List<TestCase> testCases,
      List<Category> categories) {
    this.id = id;
    this.title = title;
    this.statement = statement;
    this.timeLimitMs = timeLimitMs;
    this.memoryLimitKb = memoryLimitKb;
    this.testCases = testCases;
    this.categories = categories;
  }

  public ProblemResponseDto(Problem problem) {
    this.id = problem.getId();
    this.title = problem.getTitle();
    this.statement = problem.getStatement();
    this.timeLimitMs = problem.getTimeLimitMs();
    this.memoryLimitKb = problem.getMemoryLimitKb();
    this.testCases = problem.getTestCases();
    this.categories = problem.getCategories();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getStatement() {
    return statement;
  }

  public void setStatement(String statement) {
    this.statement = statement;
  }

  public long getTimeLimitMs() {
    return timeLimitMs;
  }

  public void setTimeLimitMs(long timeLimitMs) {
    this.timeLimitMs = timeLimitMs;
  }

  public long getMemoryLimitKb() {
    return memoryLimitKb;
  }

  public void setMemoryLimitKb(long memoryLimitKb) {
    this.memoryLimitKb = memoryLimitKb;
  }

  public List<TestCase> getTestCases() {
    return testCases;
  }

  public void setTestCases(List<TestCase> testCases) {
    this.testCases = testCases;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }
}
