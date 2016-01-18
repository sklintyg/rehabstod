package se.inera.privatlakarportal.integration.privatepractioner.services;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import se.riv.infrastructure.directory.privatepractitioner.validateprivatepractitioner.v1.rivtabp21.ValidatePrivatePractitionerResponderInterface;
import se.riv.infrastructure.directory.privatepractitioner.validateprivatepractitionerresponder.v1.ValidatePrivatePractitionerResponseType;
import se.riv.infrastructure.directory.privatepractitioner.validateprivatepractitionerresponder.v1.ValidatePrivatePractitionerType;

/**
 * Created by pebe on 2015-08-17.
 */
public class ValidatePrivatePractitionerResponderImpl implements ValidatePrivatePractitionerResponderInterface {

    @Autowired
    private IntegrationService integrationService;

    @Override
    public ValidatePrivatePractitionerResponseType validatePrivatePractitioner(String s, ValidatePrivatePractitionerType validatePrivatePractitionerType) {

        final boolean hasHsaArgument = !StringUtils.isEmpty(validatePrivatePractitionerType.getPersonHsaId());
        final boolean hasPersonArgument = !StringUtils.isEmpty(validatePrivatePractitionerType.getPersonalIdentityNumber());

        if (hasHsaArgument && hasPersonArgument) {
            throw new IllegalArgumentException("Endast ett av argumenten hsaIdentityNumber och personalIdentityNumber får vara satt.");
        }
        else if (hasHsaArgument) {
            return integrationService.validatePrivatePractitionerByHsaId(validatePrivatePractitionerType.getPersonHsaId());
        }
        else if (hasPersonArgument) {
            return integrationService.validatePrivatePractitionerByPersonId(validatePrivatePractitionerType.getPersonalIdentityNumber());
        }
        else {
            throw new IllegalArgumentException("Inget av argumenten hsaIdentityNumber och personalIdentityNumber är satt. Ett av dem måste ha ett värde.");
        }
    }
}
