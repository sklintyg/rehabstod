package se.inera.privatlakarportal.web.handlers;

import se.inera.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;

public class PrivatlakarportalRestExceptionResponse {

    private PrivatlakarportalErrorCodeEnum errorCode;

    private String message;

    public PrivatlakarportalRestExceptionResponse(PrivatlakarportalErrorCodeEnum errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public PrivatlakarportalErrorCodeEnum getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(PrivatlakarportalErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
