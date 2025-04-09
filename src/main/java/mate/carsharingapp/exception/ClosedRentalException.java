package mate.carsharingapp.exception;

public class ClosedRentalException extends RuntimeException {
    public ClosedRentalException(String message) {
        super(message);
    }
}
