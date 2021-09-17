package net.sushiclient.client.account.exceptions;

import java.io.IOException;

public class CaptchaUnsolvableException extends IOException {
    public CaptchaUnsolvableException() {
    }

    public CaptchaUnsolvableException(String message) {
        super(message);
    }

    public CaptchaUnsolvableException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptchaUnsolvableException(Throwable cause) {
        super(cause);
    }
}
