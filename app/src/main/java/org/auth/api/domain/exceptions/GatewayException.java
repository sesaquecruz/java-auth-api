package org.auth.api.domain.exceptions;

public class GatewayException extends RuntimeException {
    public static final String USER_GATEWAY_ERROR = "user gateway error";

    private GatewayException(final String message, final Throwable cause) {
        super(message, cause, true, false);
    }

    public static GatewayException with(final String message, final Throwable cause) {
        return new GatewayException(message, cause);
    }
}
