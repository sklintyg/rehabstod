package se.inera.privatlakarportal.integration.privatepractioner.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.privatlakarportal.integration.privatepractioner.services.GetPrivatePractitionerResponderImpl;
import se.inera.privatlakarportal.integration.privatepractioner.services.IntegrationService;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerresponder.v1.GetPrivatePractitionerType;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by pebe on 2015-08-19.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetPrivatePractitionerResponderTest {

    @Mock
    private IntegrationService integrationService;

    @InjectMocks
    private GetPrivatePractitionerResponderImpl getPrivatePractitionerResponder = new GetPrivatePractitionerResponderImpl();

    @Test(expected = IllegalArgumentException.class)
    public void testGetBothIds() {
        GetPrivatePractitionerType getPrivatePractitionerType = new GetPrivatePractitionerType();
        getPrivatePractitionerType.setPersonalIdentityNumber("id1");
        getPrivatePractitionerType.setPersonHsaId("id2");
        getPrivatePractitionerResponder.getPrivatePractitioner("", getPrivatePractitionerType);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNoIds() {
        GetPrivatePractitionerType getPrivatePractitionerType = new GetPrivatePractitionerType();
        getPrivatePractitionerType.setPersonalIdentityNumber(null);
        getPrivatePractitionerType.setPersonHsaId(null);
        getPrivatePractitionerResponder.getPrivatePractitioner("", getPrivatePractitionerType);
    }

    @Test
    public void testGetPersonId() {
        GetPrivatePractitionerType getPrivatePractitionerType = new GetPrivatePractitionerType();
        getPrivatePractitionerType.setPersonalIdentityNumber("id1");
        getPrivatePractitionerType.setPersonHsaId(null);
        getPrivatePractitionerResponder.getPrivatePractitioner("", getPrivatePractitionerType);
        verify(integrationService).getPrivatePractitionerByPersonId("id1");
        verifyNoMoreInteractions(integrationService);
    }

    @Test
    public void testGetHsaId() {
        GetPrivatePractitionerType getPrivatePractitionerType = new GetPrivatePractitionerType();
        getPrivatePractitionerType.setPersonalIdentityNumber(null);
        getPrivatePractitionerType.setPersonHsaId("id2");
        getPrivatePractitionerResponder.getPrivatePractitioner("", getPrivatePractitionerType);
        verify(integrationService).getPrivatePractitionerByHsaId("id2");
        verifyNoMoreInteractions(integrationService);
    }
}
