package se.inera.privatlakarportal.web.controller.api.dto;

import se.inera.privatlakarportal.common.model.Registration;

public class CreateRegistrationRequest {

    Registration registration;

    Long godkantMedgivandeVersion;

    public CreateRegistrationRequest() {
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public Long getGodkantMedgivandeVersion() {
        return godkantMedgivandeVersion;
    }

    public void setGodkantMedgivandeVersion(Long godkantMedgivandeVersion) {
        this.godkantMedgivandeVersion = godkantMedgivandeVersion;
    }
}
