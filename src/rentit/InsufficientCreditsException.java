package rentit;

public class InsufficientCreditsException extends Exception {

    public InsufficientCreditsException(String message) {
        super(message);
    }
}