package beedu.chaosmico;

public class ChaosMicoException extends RuntimeException {
    public ChaosMicoException(String message) {
        super(message);
    }

    public ChaosMicoException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChaosMicoException(Throwable cause) {
        super(cause);
    }
}
