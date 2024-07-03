package swatter.reposync.exceptions;

public class ExceptionWithStatusCode extends Exception {
    private final int statusCode;

    public ExceptionWithStatusCode(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
