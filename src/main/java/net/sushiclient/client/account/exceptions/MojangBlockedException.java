package net.sushiclient.client.account.exceptions;

public class MojangBlockedException extends StatusCodeException {
    public MojangBlockedException(int statusCode) {
        super(statusCode);
    }

    public MojangBlockedException(String message, int statusCode) {
        super(message, statusCode);
    }

    public MojangBlockedException(String message, Throwable cause, int statusCode) {
        super(message, cause, statusCode);
    }

    public MojangBlockedException(Throwable cause, int statusCode) {
        super(cause, statusCode);
    }
}
