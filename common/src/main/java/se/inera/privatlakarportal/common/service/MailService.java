package se.inera.privatlakarportal.common.service;

import se.inera.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.privatlakarportal.common.model.RegistrationStatus;
import se.inera.privatlakarportal.persistence.model.Privatlakare;

public interface MailService {

    /**
     * sendRegistrationStatusEmail is used for notifying users about the pending status of their registration via the
     * specified email address.
     * 
     * @param status
     *            {@link RegistrationStatus} Used to determine what email to send.
     * @param registration
     *            @{link Privatlakare} object with details such as email address.
     * @throws PrivatlakarportalServiceException
     *             @{link PrivatlakarportalServiceException}
     */
    public void sendRegistrationStatusEmail(RegistrationStatus status, Privatlakare privatlakare);

    public void sendHsaGenerationStatusEmail();
}
