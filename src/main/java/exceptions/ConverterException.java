package exceptions;

public class ConverterException extends RuntimeException {
    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConverterException(String message) {
        super(message);
    }
}