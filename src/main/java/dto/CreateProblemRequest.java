package dto;

import model.Category;
import model.TestCase;

import java.util.List;

public class CreateProblemRequest {
    private String title;
    private String statement;
    private long timeLimitMs;
    private long memoryLimitKb;
    private List<TestCase> testCases;
    private List<Category> categories;

    public CreateProblemRequest() {}

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
