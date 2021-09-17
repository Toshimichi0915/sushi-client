package net.sushiclient.client.account.exceptions;

public class NoConstantFactoryException extends RuntimeException {

    public NoConstantFactoryException() {
    }

    public NoConstantFactoryException(String message) {
        super(message);
    }

    public NoConstantFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoConstantFactoryException(Throwable cause) {
        super(cause);
    }

    public NoConstantFactoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
