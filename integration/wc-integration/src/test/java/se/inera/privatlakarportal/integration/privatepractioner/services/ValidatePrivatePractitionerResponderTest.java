package se.inera.privatlakarportal.integration.privatepractioner.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.privatlakarportal.integration.privatepractioner.services.IntegrationService;
import se.inera.privatlakarportal.integration.privatepractioner.services.ValidatePrivatePractitionerResponderImpl;
import se.riv.infrastructure.directory.privatepractitioner.validateprivatepractitionerresponder.v1.ValidatePrivatePractitionerType;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by pebe on 2015-08-19.
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidatePrivatePractitionerResponderTest {

    @Mock
    private IntegrationService integrationService;

    @InjectMocks
    private ValidatePrivatePractitionerResponderImpl validatePrivatePractitionerResponder = new ValidatePrivatePractitionerResponderImpl();

    @Test(expected = IllegalArgumentException.class)
    public void testGetBothIds() {
        ValidatePrivatePractitionerType validatePrivatePractitionerType = new ValidatePrivatePractitionerType();
        validatePrivatePractitionerType.setPersonalIdentityNumber("id1");
        validatePrivatePractitionerType.setPersonHsaId("id2");
        validatePrivatePractitionerResponder.validatePrivatePractitioner("", validatePrivatePractitionerType);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNoIds() {
        ValidatePrivatePractitionerType validatePrivatePractitionerType = new ValidatePrivatePractitionerType();
        validatePrivatePractitionerType.setPersonalIdentityNumber(null);
        validatePrivatePractitionerType.setPersonHsaId(null);
        validatePrivatePractitionerResponder.validatePrivatePractitioner("", validatePrivatePractitionerType);
    }

    @Test
    public void testGetPersonId() {
        ValidatePrivatePractitionerType validatePrivatePractitionerType = new ValidatePrivatePractitionerType();
        validatePrivatePractitionerType.setPersonalIdentityNumber("id1");
        validatePrivatePractitionerType.setPersonHsaId(null);
        validatePrivatePractitionerResponder.validatePrivatePractitioner("", validatePrivatePractitionerType);
        verify(integrationService).validatePrivatePractitionerByPersonId("id1");
        verifyNoMoreInteractions(integrationService);
    }

    @Test
    public void testGetHsaId() {
        ValidatePrivatePractitionerType validatePrivatePractitionerType = new ValidatePrivatePractitionerType();
        validatePrivatePractitionerType.setPersonalIdentityNumber(null);
        validatePrivatePractitionerType.setPersonHsaId("id2");
        validatePrivatePractitionerResponder.validatePrivatePractitioner("", validatePrivatePractitionerType);
        verify(integrationService).validatePrivatePractitionerByHsaId("id2");
        verifyNoMoreInteractions(integrationService);
    }
}
