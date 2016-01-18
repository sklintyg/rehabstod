package se.inera.intyg.rehabstod.web.handlers;

import se.inera.intyg.rehabstod.common.exception.RehabstodErrorCodeEnum;

public class RehabstodRestExceptionResponse {

    private RehabstodErrorCodeEnum errorCode;

    private String message;

    public RehabstodRestExceptionResponse(RehabstodErrorCodeEnum errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public RehabstodErrorCodeEnum getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(RehabstodErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
