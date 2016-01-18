package se.inera.privatlakarportal.web.controller.api.dto;

import se.inera.privatlakarportal.common.model.Registration;
import se.inera.privatlakarportal.service.model.HospInformation;

/**
 * Created by pebe on 2015-08-06.
 */
public class GetRegistrationResponse {

    private Registration registration;

    private HospInformation hospInformation;

    public GetRegistrationResponse(Registration registration, HospInformation hospInformation) {
        this.registration = registration;
        this.hospInformation = hospInformation;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public HospInformation getHospInformation() {
        return hospInformation;
    }

    public void setHospInformation(HospInformation hospInformation) {
        this.hospInformation = hospInformation;
    }
}
