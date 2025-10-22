package exception;

public class NoSuchFilmFound extends RuntimeException {
    public NoSuchFilmFound(String message) {
        super(message);
    }
}
