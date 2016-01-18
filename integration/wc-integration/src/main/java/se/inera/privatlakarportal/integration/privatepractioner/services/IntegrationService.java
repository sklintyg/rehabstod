package se.inera.privatlakarportal.integration.privatepractioner.services;

import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerresponder.v1.GetPrivatePractitionerResponseType;
import se.riv.infrastructure.directory.privatepractitioner.validateprivatepractitionerresponder.v1.ValidatePrivatePractitionerResponseType;

/**
 * Created by pebe on 2015-08-17.
 */
public interface IntegrationService {
    GetPrivatePractitionerResponseType getPrivatePractitionerByHsaId(String personHsaId);

    GetPrivatePractitionerResponseType getPrivatePractitionerByPersonId(String personalIdentityNumber);

    ValidatePrivatePractitionerResponseType validatePrivatePractitionerByHsaId(String personHsaId);

    ValidatePrivatePractitionerResponseType validatePrivatePractitionerByPersonId(String personalIdentityNumber);
}
