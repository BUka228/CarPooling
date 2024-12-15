package exceptions;

public class DataProviderException extends RuntimeException {
    public DataProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataProviderException(String message) {
        super(message);
    }
}