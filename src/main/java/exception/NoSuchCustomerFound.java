package exception;

public class NoSuchCustomerFound extends RuntimeException {
    public NoSuchCustomerFound(String message) {
        super(message);
    }
}
