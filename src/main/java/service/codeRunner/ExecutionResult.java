package service.codeRunner;

public class ExecutionResult {
    private final String output;
    private final String errorOutput;
    private final int exitCode;
    private final boolean timedOut;

    public ExecutionResult(String output, String errorOutput, int exitCode, boolean timedOut) {
        this.output = output;
        this.errorOutput = errorOutput;
        this.exitCode = exitCode;
        this.timedOut = timedOut;
    }

    public String getOutput() {
        return output;
    }

    public String getErrorOutput() {
        return errorOutput;
    }

    public int getExitCode() {
        return exitCode;
    }

    public boolean isTimedOut() {
        return timedOut;
    }
}