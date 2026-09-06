package exception;

import java.io.IOException;

public class DataAccessException extends CodeJudgeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
