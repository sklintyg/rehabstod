package se.inera.intyg.rehabstod.web.handlers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import se.inera.intyg.rehabstod.common.exception.RehabstodErrorCodeEnum;
import se.inera.intyg.rehabstod.common.exception.RehabstodServiceException;

@ControllerAdvice
public class RehabstodRestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RehabstodRestExceptionHandler.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public RehabstodRestExceptionResponse serviceExceptionHandler(HttpServletRequest request, RehabstodServiceException e) {
        LOG.warn("Internal exception occured! Internal error code: {} Error message: {}", e.getErrorCode(),
                e.getMessage());
       RehabstodRestExceptionResponse response =
                new RehabstodRestExceptionResponse(e.getErrorCode(), e.getMessage());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public RehabstodRestExceptionResponse serviceExceptionHandler(HttpServletRequest request, RuntimeException re) {
        LOG.error("Unhandled RuntimeException occured!", re);
        RehabstodRestExceptionResponse response = new RehabstodRestExceptionResponse(
                RehabstodErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, "Unhandled runtime exception");
        return response;
    }

}
