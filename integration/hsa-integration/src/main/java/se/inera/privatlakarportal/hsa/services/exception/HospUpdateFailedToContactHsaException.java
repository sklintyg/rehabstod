package se.inera.privatlakarportal.hsa.services.exception;

import javax.xml.ws.WebServiceException;

/**
 * Created by pebe on 2015-09-28.
 */
public class HospUpdateFailedToContactHsaException extends Exception {
    WebServiceException e;
    public HospUpdateFailedToContactHsaException(WebServiceException e) {
        this.e = e;
    }

    @Override
    public String getMessage() {
        return e.getMessage();
    }
}
