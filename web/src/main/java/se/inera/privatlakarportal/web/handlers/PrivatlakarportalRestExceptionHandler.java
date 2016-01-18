package se.inera.privatlakarportal.web.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.privatlakarportal.common.exception.PrivatlakarportalServiceException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class PrivatlakarportalRestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PrivatlakarportalRestExceptionHandler.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public PrivatlakarportalRestExceptionResponse serviceExceptionHandler(HttpServletRequest request, PrivatlakarportalServiceException e) {
        LOG.warn("Internal exception occured! Internal error code: {} Error message: {}", e.getErrorCode(),
                e.getMessage());
        PrivatlakarportalRestExceptionResponse response =
                new PrivatlakarportalRestExceptionResponse(e.getErrorCode(), e.getMessage());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public PrivatlakarportalRestExceptionResponse serviceExceptionHandler(HttpServletRequest request, RuntimeException re) {
        LOG.error("Unhandled RuntimeException occured!", re);
        PrivatlakarportalRestExceptionResponse response = new PrivatlakarportalRestExceptionResponse(
                PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, "Unhandled runtime exception");
        return response;
    }

}
