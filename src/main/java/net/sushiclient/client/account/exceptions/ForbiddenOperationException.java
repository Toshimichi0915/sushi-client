package net.sushiclient.client.account.exceptions;

import java.io.IOException;

public class ForbiddenOperationException extends IOException {

    public ForbiddenOperationException() {
    }

    public ForbiddenOperationException(String message) {
        super(message);
    }

    public ForbiddenOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenOperationException(Throwable cause) {
        super(cause);
    }
}
