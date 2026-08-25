package model;

import java.util.Objects;
import java.util.UUID;

public class TestCase {
    private UUID id;
    private UUID problemId;
    private String input;
    private String expectedOutput;

    public TestCase(UUID problemId, String input, String expectedOutput){
        this.id = UUID.randomUUID();
        this.problemId = problemId;
        this.input = input;
        this.expectedOutput = expectedOutput;
    }
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProblemId() {
        return problemId;
    }

    public void setProblemId(UUID problemId) {
        this.problemId = problemId;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TestCase testCase = (TestCase) o;
        return Objects.equals(id, testCase.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
