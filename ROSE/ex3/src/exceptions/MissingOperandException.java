package exceptions;

public class MissingOperandException extends SyntacticException {
    public MissingOperandException(String message) {
        super(message);
    }
}
