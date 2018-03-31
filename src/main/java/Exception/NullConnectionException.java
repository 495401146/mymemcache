package Exception;

public class NullConnectionException extends RuntimeException {
    public NullConnectionException(String msg)
    {
        super(msg);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
