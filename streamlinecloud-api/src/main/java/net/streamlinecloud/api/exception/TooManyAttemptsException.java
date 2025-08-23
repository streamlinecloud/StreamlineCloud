package net.streamlinecloud.api.exception;

public class TooManyAttemptsException extends RuntimeException {


    public TooManyAttemptsException() {
        super();
    }

    public TooManyAttemptsException(String message) {
        super(message);
    }

    public TooManyAttemptsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooManyAttemptsException(Throwable cause) {
        super(cause);
    }

    public TooManyAttemptsException(int attempts, Class<?> sourceClass) {
        super("Too many attempts (" + attempts + ") in " + sourceClass.getName());
    }

    protected TooManyAttemptsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
