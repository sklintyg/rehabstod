package se.inera.privatlakarportal.hsa.services;

import se.inera.privatlakarportal.hsa.services.exception.HospUpdateFailedToContactHsaException;
import se.inera.privatlakarportal.persistence.model.Privatlakare;
import se.inera.privatlakarportal.common.model.RegistrationStatus;

/**
 * Created by pebe on 2015-09-03.
 */
public interface HospUpdateService {

    void scheduledUpdateHospInformation();

    void updateHospInformation();

    RegistrationStatus updateHospInformation(Privatlakare privatlakare, boolean shouldRegisterInCertifier)
            throws HospUpdateFailedToContactHsaException;

    void checkForUpdatedHospInformation(Privatlakare privatlakare);
}
