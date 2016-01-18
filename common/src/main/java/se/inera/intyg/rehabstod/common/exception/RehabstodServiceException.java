package se.inera.intyg.rehabstod.common.exception;

public class RehabstodServiceException extends RuntimeException {
    private final RehabstodErrorCodeEnum errorCode;

    public RehabstodServiceException(RehabstodErrorCodeEnum errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public RehabstodErrorCodeEnum getErrorCode() {
        return errorCode;
    }
}
