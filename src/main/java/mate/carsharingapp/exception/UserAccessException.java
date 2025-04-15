package mate.carsharingapp.exception;

public class UserAccessException extends RuntimeException {
    public UserAccessException(String message) {
        super(message);
    }
}
