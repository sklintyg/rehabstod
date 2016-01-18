package se.inera.privatlakarportal.web.controller.api.dto;

import se.inera.privatlakarportal.service.model.SaveRegistrationResponseStatus;

/**
 * Created by pebe on 2015-08-17.
 */
public class SaveRegistrationResponse {

    private SaveRegistrationResponseStatus status;

    public SaveRegistrationResponse(SaveRegistrationResponseStatus status) {
        this.status = status;
    }

    public SaveRegistrationResponseStatus getStatus() {
        return status;
    }

    public void setStatus(SaveRegistrationResponseStatus status) {
        this.status = status;
    }
}
