package exception;

public class ProblemNotFoundException extends EntityNotFoundException {
    public ProblemNotFoundException(String message) {
        super(message);
    }
}
