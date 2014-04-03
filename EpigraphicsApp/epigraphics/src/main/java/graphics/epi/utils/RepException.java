package graphics.epi.utils;

/**
 * Thrown when the representation invariant of an object is violated
 * (for debugging, should never happen)
 */
public class RepException extends RuntimeException {
    public RepException(String message) {
        super(message);
    }
}
