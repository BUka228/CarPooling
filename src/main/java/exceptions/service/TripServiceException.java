package exceptions.service;

public class TripServiceException extends Exception {
    public TripServiceException(String message) {
        super(message);
    }

    public TripServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}