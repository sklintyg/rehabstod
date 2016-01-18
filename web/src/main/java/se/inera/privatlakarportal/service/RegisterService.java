package se.inera.privatlakarportal.service;

import se.inera.privatlakarportal.common.model.Registration;
import se.inera.privatlakarportal.common.model.RegistrationStatus;
import se.inera.privatlakarportal.service.model.*;

/**
 * Created by pebe on 2015-06-25.
 */
public interface RegisterService {

    RegistrationStatus createRegistration(Registration registration, Long godkantMedgivandeVersion);

    SaveRegistrationResponseStatus saveRegistration(Registration registration);

    HospInformation getHospInformation();

    RegistrationWithHospInformation getRegistration();

    boolean removePrivatlakare(String personId);

    void injectHsaInterval(int hsaIdNotificationInterval);
}
