package model;

import java.util.Objects;
import java.util.UUID;

public class TestCase {
    private UUID id;
    private UUID problemId;
    private String input;
    private String expectedOutput;
    public TestCase(){

    }
    public TestCase( String input, String expectedOutput){
        this.input = input;
        this.expectedOutput = expectedOutput;
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
