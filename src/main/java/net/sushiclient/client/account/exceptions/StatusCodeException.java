package net.sushiclient.client.account.exceptions;

import java.io.IOException;

public class StatusCodeException extends IOException {
    private final int statusCode;

    public StatusCodeException(int statusCode) {
        this.statusCode = statusCode;
    }

    public StatusCodeException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public StatusCodeException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public StatusCodeException(Throwable cause, int statusCode) {
        super(cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
