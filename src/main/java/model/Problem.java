package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Problem {
    private UUID id;
    private String title;
    private String statement;
    private long timeLimitMs;
    private long memoryLimitKb;
    private List<TestCase> testCases;
    private List<Category> categories;

    public Problem() {
        this.testCases = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    public Problem(String title, String statement, long timeLimitMs, long memoryLimitKb) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.statement = statement;
        this.timeLimitMs = timeLimitMs;
        this.memoryLimitKb = memoryLimitKb;
        this.testCases = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    public void addTestCase(TestCase testCase) {
        this.testCases.add(testCase);
    }

    public void addCategory(Category category) {
        this.categories.add(category);
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Problem problem = (Problem) o;
        return Objects.equals(id, problem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
