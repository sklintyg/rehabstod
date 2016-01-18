package se.inera.privatlakarportal.web.controller.api.dto;

import se.inera.privatlakarportal.common.model.Registration;

/**
 * Created by pebe on 2015-08-17.
 */
public class SaveRegistrationRequest {

    Registration registration;

    public SaveRegistrationRequest() {
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }
}
