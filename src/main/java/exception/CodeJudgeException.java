package exception;

public class CodeJudgeException extends RuntimeException {
    public CodeJudgeException(String message) {
        super(message);
    }
    public CodeJudgeException(String message, Throwable cause) {
        super(message, cause);
    }}
