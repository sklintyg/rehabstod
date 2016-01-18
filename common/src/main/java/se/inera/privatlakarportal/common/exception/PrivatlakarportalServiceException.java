package se.inera.privatlakarportal.common.exception;

public class PrivatlakarportalServiceException extends RuntimeException {
    private final PrivatlakarportalErrorCodeEnum errorCode;

    public PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PrivatlakarportalErrorCodeEnum getErrorCode() {
        return errorCode;
    }
}
