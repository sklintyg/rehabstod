package se.inera.privatlakarportal.integration.privatepractioner.services;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitioner.v1.rivtabp21.GetPrivatePractitionerResponderInterface;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerresponder.v1.GetPrivatePractitionerResponseType;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerresponder.v1.GetPrivatePractitionerType;

/**
 * Created by pebe on 2015-08-17.
 */
public class GetPrivatePractitionerResponderImpl implements GetPrivatePractitionerResponderInterface {

    @Autowired
    private IntegrationService integrationService;

    @Override
    public GetPrivatePractitionerResponseType getPrivatePractitioner(String s, GetPrivatePractitionerType getPrivatePractitionerType) {

        final boolean hasHsaArgument = !StringUtils.isEmpty(getPrivatePractitionerType.getPersonHsaId());
        final boolean hasPersonArgument = !StringUtils.isEmpty(getPrivatePractitionerType.getPersonalIdentityNumber());

        if (hasHsaArgument && hasPersonArgument) {
            throw new IllegalArgumentException("Endast ett av argumenten hsaIdentityNumber och personalIdentityNumber får vara satt.");
        }
        else if (hasHsaArgument) {
            return integrationService.getPrivatePractitionerByHsaId(getPrivatePractitionerType.getPersonHsaId());
        }
        else if (hasPersonArgument) {
            return integrationService.getPrivatePractitionerByPersonId(getPrivatePractitionerType.getPersonalIdentityNumber());
        }
        else {
            throw new IllegalArgumentException("Inget av argumenten hsaIdentityNumber och personalIdentityNumber är satt. Ett av dem måste ha ett värde.");
        }
    }
}
